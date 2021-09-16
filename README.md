[![Build](https://github.com/eskaton/asn4j/actions/workflows/build.yaml/badge.svg)](https://github.com/eskaton/asn4j/actions/workflows/build.yaml) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=eskaton_asn4j&metric=coverage)](https://sonarcloud.io/dashboard?id=eskaton_asn4j)

ASN4J - ASN.1 compiler and runtime for Java
========

## Supported types

- [x] BIT STRING
- [x] BMPString
- [x] BOOLEAN
- [x] CHOICE 
- [ ] EMBEDDED PDV
- [x] ENUMERATED
- [ ] EXTERNAL
- [x] GeneralString
- [x] GraphicString
- [x] IA5String
- [x] INTEGER
- [x] NULL
- [x] NumericString
- [x] OBJECT IDENTIFIER
- [x] OCTET STRING
- [x] OID-IRI
- [x] PrintableString
- [x] REAL
- [x] RELATIVE-OID
- [x] RELATIVE-OID-IRI
- [x] SEQUENCE
- [x] SEQUENCE OF 
- [x] SET
- [x] SET OF 
- [x] TeletexString
- [ ] TIME
- [x] UniversalString
- [x] UTF8String
- [x] VideotexString
- [x] VisibleString

## Supported constraints

| Type (or derived from such a type by tagging or subtyping) | Single value | Contained<br>subtype | Value<br>range | Size | Permitted<br>alphabet | Type<br>constraint | Inner<br>constraint<br>subtyping | Pattern<br>constraint |
|----------------------------------------------------|--------|-----------|-------|------|-----------|------------|------------|------------|
| Bit string                                         | yes    | yes       | -     | yes  | -         | -          | -          | -          |
| Boolean                                            | yes    | yes       | -     | -    | -         | -          | -          | -          |
| Choice                                             | yes    | yes       | -     | -    | -         | -          | yes        | -          |
| Embedded-pdv                                       | no     | -         | -     | -    | -         | -          | no         | -          |
| Enumerated                                         | yes    | yes       | -     | -    | -         | -          | -          | -          |
| External                                           | no     | -         | -     | -    | -         | -          | no         | -          |
| Instance-of                                        | no     | no        | -     | -    | -         | -          | no         | -          |
| Integer                                            | yes    | yes       | yes   | -    | -         | -          | -          | -          |
| Null                                               | yes    | yes       | -     | -    | -         | -          | -          | -          |
| Object class field type                            | no     | no        | -     | -    | -         | -          | -          | -          |
| Object descriptor                                  | no     | no        | -     | no   | no        | -          | -          | -          |
| Object identifier                                  | yes    | yes       | -     | -    | -         | -          | -          | -          |
| Octet string                                       | yes    | yes       | -     | yes  | -         | -          | -          | -          |
| OID internationalized resource identifier          | yes    | yes       | -     | -    | -         | -          | -          | -          |
| open type                                          | -      | -         | -     | -    | -         | no         | -          | -          |
| Real                                               | no     | no        | no    | -    | -         | -          | no         | -          |
| Relative object identifier                         | yes    | yes       | -     | -    | -         | -          | -          | -          |
| Relative OID internationalized resource identifier | yes    | yes       | -     | -    | -         | -          | -          | -          |
| Restricted character string types                  | no     | no        | no    | no   | no        | -          | -          | no         |
| - VisibleString                                    | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - GeneralString                                    | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - GraphicString                                    | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - IA5String                                        | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - VideotexString                                   | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - TeletexString                                    | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - PrintableString                                  | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - NumericString                                    | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - UTF8String                                       | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - UniversalString                                  | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| - BMPString                                        | yes    | yes       | yes   | yes  | yes       | -          | -          | no         |
| Sequence                                           | yes    | yes       | -     | -    | -         | -          | yes        | -          |
| Sequence-of                                        | yes    | yes       | -     | yes  | -         | -          | yes        | -          |
| Set                                                | yes    | yes       | -     | -    | -         | -          | yes        | -          |
| Set-of                                             | yes    | yes       | -     | yes  | -         | -          | yes        | -          |
| GeneralizedTime and UTCTime types                  | no     | no        | -     | -    | -         | -          | -          | -          |
| Unrestricted character string type                 | no     | -         | -     | no   | -         | -          | no         | -          |
