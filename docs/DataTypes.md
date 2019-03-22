# Data Types

DataHelix currently recognises three distinct data types. Keeping this set small is a deliberate goal; it would be possible to have types like _FirstName_ or _Postcode_, but instead these are considered specialisations of the _String_ type, so they can be constrained by the normal string operators (e.g. a user could generate all first names shorter than 10 characters, starting with a vowel).

## Numeric

In principle, any real number. In practice, any number that can be represented in a Java [BigDecimal](https://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html) object.

In profile files, numbers must be expressed as JSON numbers, without quotation marks.

### Numeric granularity

The granularity of a numeric field is a measure of how small the distinctions in that field can be; it is the smallest positive number of which every valid value is a multiple. For instance:

- if a numeric field has a granularity of 1, it can only be satisfied with multiples of 1, a.k.a. integers
- if a numeric field has a granularity of 0.1, it can be satified by 1, or 1.1, but not 1.11

Granularities must be powers of ten less than or equal to one (1, 0.1, 0.01, etc). Granularities outside these restrictions could be potentially useful (e.g. a granularity of 2 would permit only even numbers) but are not currently supported.

Numeric fields currently default to a granularity of 1. Post-[#135](https://github.com/ScottLogic/datahelix/issues/135), they'll default to an extremely small granularity.

Note that granularity concerns which values are valid, not how they're presented. If the goal is to enforce a certain number of decimal places in text output, the `formattedAs` operator is required. See: [What's the difference between formattedAs and granularTo?](./FrequentlyAskedQuestions.md#whats-the-difference-between-formattedas-and-granularto)

## Strings

Strings are sequences of unicode characters. Currently, only characters from the [Basic Multilingual Plane](https://en.wikipedia.org/wiki/Plane_(Unicode)#Basic_Multilingual_Plane) (Plane 0) are supported.

## Temporal

Temporal values represent specific moments in time, and are specified in profiles through specialised objects:

```javascript
{ "date": "2000-01-01T09:00:00.000" }
```

The format is a subset of [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601); the date and time must be fully specified as above, 
with precisely 3 digits of sub-second precision, plus an optional offset specifier of either "Z" or a "+HH" format. 
Values have the same maximum precision as Java's [OffsetDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html) class.

Temporal values can be in the range `0001-01-01T00:00:00.000` to `9999-12-31T23:59:59.999`
that is **_`midnight on the 1st January 0001`_** to **_`1 millisecond to midnight on the 31 December 9999`_**

Temporal values are by default output per the user's locale, adjusted to their time zone.

### Temporal granularity

Temporal values currently have granularities derived from the size of their range. Future work ([#141](https://github.com/ScottLogic/datahelix/issues/141)) will make this configurable.