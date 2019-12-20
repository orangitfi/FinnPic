package fi.orangit

/**
 * This package contains the Finnish Personal Identity Code implementation. See the specification at [[https://vrk.fi/en/personal-identity-code1]].
 *
 * Some terminology:
 *   - "PIC" refers to the Personal Identity Code as a concept.
 *   - "Pic" refers to the class [[fi.orangit.fpic.Pic]].
 *
 * Some examples of [[fi.orangit.fpic.Pic]] creation:
 * {{{
 * >>> import fi.orangit.fpic.Pic
 *
 * >>> Pic.fromString("290877-1639")
 * Right(290877-1639)
 *
 * >>> Pic.fromString("290877-163")
 * Left(Invalid PIC: '290877-163'. PIC should have 11 characters, but was 10 characters.)
 *
 * >>> Pic.fromString("290877-1638")
 * Left(Invalid PIC: '290877-1638'. The control character ('8') is wrong: it should be '9'.)
 *
 * >>> Pic.fromStringUnsafe("290877-1639")
 * 290877-1639
 *
 * // Pic.fromStringU is just a shorter alias for Pic.fromStringUnsafe.
 * >>> Pic.fromStringU("290877-1639")
 * 290877-1639
 *
 * // If you give Pic.fromStringUnsafe (or Pic.fromStringU) an invalid PIC, they throw an IllegalArgumentException.
 * }}}
 */
package object fpic
