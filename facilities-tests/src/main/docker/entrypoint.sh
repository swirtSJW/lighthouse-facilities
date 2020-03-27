#!/usr/bin/env bash

set -o pipefail

[ -z "$SENTINEL_BASE_DIR" ] && SENTINEL_BASE_DIR=/sentinel
cd $SENTINEL_BASE_DIR


#============================================================

# The prefix used for modules, e.g. lighthouse or health-apis
MODULE_PREFIX=lighthouse

# The name of the module containing tests (with out the prefix)
TEST_MODULE_NAME=facilities-tests

# The location of the categories defined within the test module
TEST_MODULE_CATEGORIES_LOCATION=gov/va/api/lighthouse/facilities/tests/categories/

# The name of module "main" jar, this is where categories and other reusable classes are defined
MAIN_JAR=$(find -maxdepth 1 -name "${TEST_MODULE_NAME}-*.jar" -a -not -name "${TEST_MODULE_NAME}-*-tests.jar")

# The name of the modules "tests" jar, this is where tests are actually defined
TESTS_JAR=$(find -maxdepth 1 -name "${TEST_MODULE_NAME}-*-tests.jar")

# Categories of tests to run for smoke tests (class name csv)
SENTINEL_SMOKE_TEST_CATEGORY=gov.va.api.lighthouse.facilities.tests.categories.FacilityById

# Categories of tests to run for regressin tests (class name csv)
SENTINEL_REGRESSION_TEST_CATEGORY=

# Environment variables that are required to run
REQUIRED_ENV_VARIABLES=(
  "K8S_LOAD_BALANCER" "K8S_ENVIRONMENT" "SENTINEL_ENV" \
  "SENTINEL_SMOKE_TEST_CATEGORY" \
  "CLIENT_KEY" "API_KEY"
)

#
# Assume defaults for service locations to be on the load balancer.
#
if [ -z "$SENTINEL_ENV" ]; then SENTINEL_ENV=$K8S_ENVIRONMENT; fi
if [ -z "$FACILITIES_URL" ]; then FACILITIES_URL=https://$K8S_LOAD_BALANCER; fi
if [ -z "$FACILITIES_COLLECTOR_URL" ]; then FACILITIES_COLLECTOR_URL=https://$K8S_LOAD_BALANCER; fi
#============================================================


SYSTEM_PROPERTIES=()
#
# These may be optional set to reduce the tests that are ran (class name csv)
# For regress or smoke tests, they will be set automatically based on sentinel categories.
#
EXCLUDE_CATEGORY=
INCLUDE_CATEGORY=

#============================================================
if [ ! -f "$MAIN_JAR" ]; then echo "Cannot find main jar: $MAIN_JAR"; exit 1; fi
if [ ! -f "$TESTS_JAR" ]; then echo "Cannot find tests jar: $TESTS_JAR"; exit 1; fi

usage() {
cat <<EOF
Commands
  list-tests
  list-categories
  test [--include-category <category>] [--exclude-category <category>] [--trust <host>] [-Dkey=value] <name> [name] [...]
  smoke-test
  regression-test


Example
  test\
    --exclude-category gov.va.api.health.sentinel.categories.Local \
    --include-category gov.va.api.health.sentinel.categories.Manual \
    --trust example.something.elb.amazonaws.com \
    -Dclient-key=12345 \
    gov.va.api.health.dataquery.tests.UsingMagicPatientCrawlerTest

Docker Run Examples
  docker run --rm --init --network=host \
    --env-file qa.testvars --env K8S_LOAD_BALANCER=example.com --env K8S_ENVIRONMENT=qa \
    vasdvp/${MODULE_PREFIX}-${TEST_MODULE_NAME}:latest smoke-test

  docker run --rm --init --network=host \
    --env-file lab.testvars --env K8S_LOAD_BALANCER=example.com --env K8S_ENVIRONMENT=lab \
    vasdvp/${MODULE_PREFIX}-${TEST_MODULE_NAME}:1.0.210 regression-test
$1
EOF
exit 1
}

trustServer() {
  local host=$1
  curl -sk https://$host > /dev/null 2>&1
  [ $? == 6 ] && return
  echo "Trusting $host"
  local cacertsDir="$JAVA_HOME/jre/lib/security/cacerts"
  [ -f "$JAVA_HOME/lib/security/cacerts" ] && cacertsDir="$JAVA_HOME/lib/security/cacerts"
  keytool -printcert -rfc -sslserver $host > $host.pem
  keytool \
    -importcert \
    -file $host.pem \
    -alias $host \
    -keystore $cacertsDir \
    -storepass changeit \
    -noprompt
}

defaultTests() {
  doListTests | grep 'IT$'
}

doTest() {
  local tests="$@"
  [ -z "$tests" ] && tests=$(defaultTests)
  local filter
  [ -n "$EXCLUDE_CATEGORY" ] && filter+=" --filter=org.junit.experimental.categories.ExcludeCategories=$EXCLUDE_CATEGORY"
  [ -n "$INCLUDE_CATEGORY" ] && filter+=" --filter=org.junit.experimental.categories.IncludeCategories=$INCLUDE_CATEGORY"
  local noise="org.junit"
  noise+="|groovy.lang.Meta"
  noise+="|io.restassured.filter"
  noise+="|io.restassured.internal"
  noise+="|java.lang.reflect"
  noise+="|java.net"
  noise+="|org.apache.http"
  noise+="|org.codehaus.groovy"
  noise+="|sun.reflect"
  java -cp "$(pwd)/*" ${SYSTEM_PROPERTIES[@]} org.junit.runner.JUnitCore $filter $tests \
    | grep -vE "^	at ($noise)"

  # Exit on failure otherwise let other actions run.
  [ $? != 0 ] && exit 1
}

doListTests() {
  jar -tf $TESTS_JAR \
    | grep -E '(IT|Test)\.class' \
    | sed 's/\.class//' \
    | tr / . \
    | sort
}

doListCategories() {
  jar -tf $MAIN_JAR \
    | grep -E "gov/va/api/health/sentinel/categories/.*\.class|${TEST_MODULE_CATEGORIES_LOCATION}.*\.class" \
    | sed 's/\.class//' \
    | tr / . \
    | sort
}

doSmokeTest() {
  setupForAutomation

  INCLUDE_CATEGORY=$SENTINEL_SMOKE_TEST_CATEGORY
  doTest
}

doRegressionTest() {
  setupForAutomation

  INCLUDE_CATEGORY=$SENTINEL_REGRESSION_TEST_CATEGORY
  doTest

}


checkVariablesForAutomation() {
  # Check out required deployment variables and data query specific variables.
  for param in ${REQUIRED_ENV_VARIABLES[@]}; do
    [ -z ${!param} ] && usage "Variable $param must be specified."
  done
}

setupForAutomation() {
  checkVariablesForAutomation

  trustServer $K8S_LOAD_BALANCER

  SYSTEM_PROPERTIES+=( "-Dsentinel=$SENTINEL_ENV" )
  SYSTEM_PROPERTIES+=( "-Dapikey=$API_KEY" )
  SYSTEM_PROPERTIES+=(
    "-Dsentinel.facilities.url=$FACILITIES_URL"
    "-Dsentinel.facilities.api-path=$FACILITIES_API_PATH"
    "-Dsentinel.facilities-management.url=$FACILITIES_URL"
    "-Dsentinel.facilities-management.api-path=$FACILITIES_MANAGEMENT_API_PATH"
  )
  SYSTEM_PROPERTIES+=(
    "-Dsentinel.facilities-collector.url=$FACILITIES_COLLECTOR_URL"
    "-Dsentinel.facilities-collector.api-path=$FACILITIES_COLLECTOR_API_PATH"
  )
}

ARGS=$(getopt -n $(basename ${0}) \
    -l "exclude-category:,include-category:,debug,help,trust:,skip-crawler" \
    -o "e:i:D:hs" -- "$@")
[ $? != 0 ] && usage
eval set -- "$ARGS"
while true
do
  case "$1" in
    -e|--exclude-category) EXCLUDE_CATEGORY=$2;;
    -i|--include-category) INCLUDE_CATEGORY=$2;;
    -D) SYSTEM_PROPERTIES+=( "-D$2");;
    --debug) set -x;;
    -h|--help) usage "halp! what this do?";;
    --trust) trustServer $2;;
    --) shift;break;;
  esac
  shift;
done

[ $# == 0 ] && usage "No command specified"
COMMAND=$1
shift

case "$COMMAND" in
  t|test) doTest $@;;
  lc|list-categories) doListCategories;;
  lt|list-tests) doListTests;;
  s|smoke-test) doSmokeTest;;
  r|regression-test) doRegressionTest;;
  *) usage "Unknown command: $COMMAND";;
esac

exit 0
