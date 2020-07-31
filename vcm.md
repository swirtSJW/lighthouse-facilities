# New endpoints to support VCM

Two sample records to save:

urgent-contact-1.json

```
{
  "id": "45815",
  "facility_id": "vha_534",
  "clinic_name": "CHS NEUROSURGERY VARMA",
  "clinic_specialty": "NEUROSURGERY",
  "administrator": {
    "name": "Bob Nelson",
    "email": "bob.nelson@foo.bar",
    "phone": {
      "number": "123-456-7890"
    }
  },
  "note": "2ND FL SPECIALTY, RM D219",
  "phone_numbers": [{
      "label": "Main",
      "number": "843-577-5011"
    }, {
      "number": "123-456-7890",
      "extension": "9999"
    }, {
      "number": "123-456-7890",
      "extension": "9999"
    }
  ]
}
```

urgent-contact-2.json

```
{
  "id": "45825",
  "facility_id": "vha_534",
  "clinic_name": "CHS GEN SURG H&P",
  "clinic_specialty": "GENERAL SURGERY",
  "administrator": {
    "name": "Bob Nelson",
    "email": "bob.nelson@foo.bar",
    "phone": {
      "number": "123-456-7890"
    }
  },
  "note": "1st FL, Specialty Clinic",
  "phone_numbers": [{
      "label": "Main",
      "number": "843-577-5011"
    }, {
      "number": "123-456-7890",
      "extension": "9999"
    }, {
      "number": "123-456-7890",
      "extension": "9999"
    }
  ]
}
```

### Notes

ID:

* id is a *globally unique* string that uniquely identifies the record
* In the examples above, this is locationSID
* This name can be more specific if it will always correspond to a known field like locationSID
* ID is expected be permanent; it should not change once a record is created

Facility ID:

* facility_id refers to the facility with which this record is associated
* same facility ID the rest of Facilities API uses
* each facility may have zero, one, or many urgent contact records

Misc:

* optional fields: clinic_name, clinic_specialty, phone.label, phone.extension
* clinic name is the name within the facility, e.g. "what's posted on the door"
* clinic specialty is freeform text (no taxonomy)
* note corresponds to 'location specific emergency guidance' in the UI mock-ups
* note limited to 1000 characters
* at least one phone number is required

### Open Questions

* Is 'clinic_service' a more appropriate name than 'clinic_specialty'? (Note this is clinician-facing, not veteran-facing.)
* Should 'note' have a more specific name? (Closer to `location specific emergency guidance'?)
* Is the phone label field necessary?
* Does each facility have only one administrator responsible for all its urgent contact numbers?
    If so, it is redundant it to specify it on every record; we may want to manage administrator data separately
* Administrator is PII. We would prefer not to store that in Facilities API. Could administrator be managed in VCM instead?

## Example operations

### Save (or update) an individual record:

`curl -X POST -HContent-Type:application/json -d @urgent-contact-1.json https://blue.qa.lighthouse.va.gov/va_facilities/v0/urgent-contact`

`curl -X POST -HContent-Type:application/json -d @urgent-contact-2.json https://blue.qa.lighthouse.va.gov/va_facilities/v0/urgent-contact`

### Read (single record by ID): 
`curl https://blue.qa.lighthouse.va.gov/va_facilities/v0/urgent-contact/45815`

response:

```
{
  "id": "45815",
  "facility_id": "vha_534",
  "clinic_name": "CHS NEUROSURGERY VARMA",
  "clinic_specialty": "NEUROSURGERY",
  "administrator": {
    "name": "Bob Nelson",
    "email": "bob.nelson@foo.bar",
    "phone": {
      "number": "123-456-7890"
    }
  },
  "note": "2ND FL SPECIALTY, RM D219",
  "phone_numbers": [{
      "label": "Main",
      "number": "843-577-5011"
    }, {
      "number": "123-456-7890",
      "extension": "9999"
    }, {
      "number": "123-456-7890",
      "extension": "9999"
    }
  ],
  "last_updated": "2020-07-20T15:33:25.186742300Z"
}
```

### Search (all associated records for a facility ID):

`curl https://blue.qa.lighthouse.va.gov/va_facilities/v0/urgent-contact?facility_id=vha_534`

response:

```
{
  "urgentContacts": [{
      "id": "45815",
      "facility_id": "vha_534",
      "clinic_name": "CHS NEUROSURGERY VARMA",
      "clinic_specialty": "NEUROSURGERY",
      "administrator": {
        "name": "Bob Nelson",
        "email": "bob.nelson@foo.bar",
        "phone": {
          "number": "123-456-7890"
        }
      },
      "note": "2ND FL SPECIALTY, RM D219",
      "phone_numbers": [{
          "label": "Main",
          "number": "843-577-5011"
        }, {
          "number": "123-456-7890",
          "extension": "9999"
        }, {
          "number": "123-456-7890",
          "extension": "9999"
        }
      ],
      "last_updated": "2020-07-20T15:33:30.121738900Z"
    }, {
      "id": "45825",
      "facility_id": "vha_534",
      "clinic_name": "CHS GEN SURG H&P",
      "clinic_specialty": "GENERAL SURGERY",
      "administrator": {
        "name": "Bob Nelson",
        "email": "bob.nelson@foo.bar",
        "phone": {
          "number": "123-456-7890"
        }
      },
      "note": "1st FL, Specialty Clinic",
      "phone_numbers": [{
          "label": "Main",
          "number": "843-577-5011"
        }, {
          "number": "123-456-7890",
          "extension": "9999"
        }, {
          "number": "123-456-7890",
          "extension": "9999"
        }
      ],
      "last_updated": "2020-07-20T15:33:25.186742300Z"
    }
  ]
}
```
