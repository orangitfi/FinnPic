[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&utm_medium=referral&utm_content=orangitfi/finnish-personal-identity-code&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8f19681119574ecd96ef6790b29dcde2)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=orangitfi/finnish-personal-identity-code&amp;utm_campaign=Badge_Grade)
![sbt test](https://github.com/orangitfi/finnish-personal-identity-code/workflows/sbt-test/badge.svg)

# finnish-personal-identity-code

A Scala implementation of Finnish PIC (Personal Identity Code)("henkilötunnus" in Finnish).

See the specification here: [https://vrk.fi/en/personal-identity-code1](https://vrk.fi/en/personal-identity-code1).

## Building

Full build with test coverage (which should stay at 100%) measurement:

> sbt clean coverage test coverageReport
