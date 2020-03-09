[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=orangitfi/finnish-personal-identity-code&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=orangitfi/finnish-personal-identity-code&amp;utm_campaign=Badge_Grade)
![sbt test](https://github.com/orangitfi/finnish-personal-identity-code/workflows/sbt-test/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.finnpic/finnpic/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.finnpic/finnpic)

# FinnPic

<img src="assets/Flag_of_Finland.svg" alt="Flag of Finland" width="15%">

A Scala implementation of Finnish PIC (Personal Identity Code)("henkilötunnus" in Finnish).

See the specification here: [https://vrk.fi/en/personal-identity-code1](https://vrk.fi/en/personal-identity-code1).

## Building

You need to have [SDKMAN](https://sdkman.io/) and [direnv](https://direnv.net/) installed.

Put this in your `~/.direnvrc`:

```shell script
# https://github.com/direnv/direnv/issues/420
# iterate on pairs of [candidate] [version] and invoice `sdk use` on each of them
use_sdk() {
  [[ -s "${SDKMAN_DIR}/bin/sdkman-init.sh" ]] && source "${SDKMAN_DIR}/bin/sdkman-init.sh"

  while (( "$#" >= 2 )); do
    local candidate=$1
    local candidate_version=$2
    sdk use $candidate $candidate_version

    shift 2
  done
}
```

`cd` to the root of the project and say `direnv allow`.

Install the required versions of Java, Scala and Sbt using SDKMAN.

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
val p = Pic.fromString("070377-281V")
// ^ p is now Right(Pic(...))
```

The safe way, failing:

```scala
p = Pic.fromString("foo")
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

## Publishing a new version

Open an sbt shell and run:

> `+ coverageOff`

> `+ clean`

> `+ publishSigned`

Don't forget the plus signs (for cross compilation and cross publishing)!

As for your GPG keys and stuff, see https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html and https://github.com/keybase/keybase-issues/issues/2798.

## Supporters

This project is proudly supported by [Orangit Oy](https://orangit.fi).

<a href="https://orangit.fi">
  <img src="assets/orangit_logo_web.svg" alt="Orangit Oy" width="15%">
</a>
