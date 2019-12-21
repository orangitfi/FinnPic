[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=orangitfi/finnish-personal-identity-code&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=orangitfi/finnish-personal-identity-code&amp;utm_campaign=Badge_Grade)
![sbt test](https://github.com/orangitfi/finnish-personal-identity-code/workflows/sbt-test/badge.svg)

# FinnPic

<img src="assets/Flag_of_Finland.svg" alt="Flag of Finland" width="15%">

A Scala implementation of Finnish PIC (Personal Identity Code)("henkilötunnus" in Finnish).

See the specification here: [https://vrk.fi/en/personal-identity-code1](https://vrk.fi/en/personal-identity-code1).

## Building

Full build with test coverage (which should stay at 100%) measurement:

> sbt clean coverage test coverageReport

## Usage

```scala
// The safe way (returning Either[String, Pic].
// Use this if the PIC value (the string) is coming
// from a user or some other undependable source.
var p: Either[String, Pic] = Pic.fromString("290877-1639")
// ^ p is now Right(Pic(...))
p = Pic.fromString("foo")
// ^ p is now Left("some error message")

// The unsafe way (returning a Pic, and throwing
// IllegalArgumentException if the PIC value is
// invalid). Use this if the PIC value comes from
// a dependable source or you are willing to
// handle exceptions.
var p: Pic = Pic.fromStringUnsafe("290877-1639")
// ^ p is now Pic(...)
p = Pic.fromStringUnsafe("foo")
// ^ throws IllegalArgumentException

// There is also Pic.fromStringU, which is just an
// alias for Pic.fromStringUnsafe.
```

## Supporters

This project is proudly supported by [Orangit Oy](https://orangit.fi).

<a href="https://orangit.fi">
  <img src="assets/orangit_logo_web.svg" alt="Orangit Oy" width="15%">
</a>
