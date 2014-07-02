# The *Official* Bitsplit API

## Actions
Whatever the UI is, it will be sending "action" objects of the schema `{:type :keyword ...}`, where `:type` is

* `:add-address` add a new address to a given split, needs `:parent` and `:address` at minimum, can autofill/calculate `:percent`
* `:remove-address` all the same as above, but doesn't use `:percent`

## Ch-Ch-Ch-Changes