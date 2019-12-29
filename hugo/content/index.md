# FinnPic

[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=orangitfi/finnish-personal-identity-code&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=orangitfi/finnish-personal-identity-code&amp;utm_campaign=Badge_Grade)
![sbt test](https://github.com/orangitfi/finnish-personal-identity-code/workflows/sbt-test/badge.svg)

<a href="https://vrk.fi/en/personal-identity-code1">
  <img alt="Flag of Finland" src="/images/Flag_of_Finland.svg" width="15%">
</a>

A Scala implementation of Finnish PIC (Personal Identity Code)("henkilötunnus" in Finnish).

See the specification here: [https://vrk.fi/en/personal-identity-code1](https://vrk.fi/en/personal-identity-code1).

## Building

Full build with test coverage (which should stay at 100%) measurement:

> sbt clean coverage test coverageReport

## Usage

### Construction

There is a safe way and an unsafe way to make instances of *Pic*.

The safe way is to use `Pic.fromString`, which returns instances of
`Either` (in Scala standard library). `Left(errorMessage)` means
that the given PIC was invalid, the error message inside tells you
why. `Right(pic)` means the PIC was valid and now you can obtain 
the value from the right side of the `Either`. This is a very common
pattern in functional programming.

Use the safe way if the PIC value (the string) is coming from a user
or some other undependable source.

The safe way, used successfully on a valid PIC:

```scala
// The safe way, success:
val p = Pic("070377-281V")
// ^ p is now Right(Pic(...))
```

The safe way, failing:

```scala
p = Pic("foo")
// ^ p is now Left("some error message")
```

The unsafe way is to use ```Pic.fromStringUnsafe```, or its alias ```Pic.fromStringU```
(which does the same thing, but is shorter to type and read). The unsafe variants return a
*Pic* object directly on success (the PIC is valid). If the PIC is not valid, these functions
throw an exception (a java.lang.IllegalArgumentException, to be more precise).

Use the unsafe way when the PIC value comes from a dependable source (for example, from
a database column where you know that only valid values are stored).

```scala
val p: Pic = Pic.fromStringUnsafe("070377-281V")
// ^ p is now Pic(...)

p = Pic.fromStringUnsafe("foo")
// ^ throws IllegalArgumentException

// There is also Pic.fromStringU, which is just an
// alias for Pic.fromStringUnsafe.
```

## Supporters

This project is proudly supported by [Orangit Oy](https://orangit.fi). [We're hiring!](https://orangit.fi/careers/)

<a href="https://orangit.fi">
  <img alt="Orangit logo" src="/images/orangit_logo_web.svg" width="15%">
</a>
