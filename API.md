# The *Official* Bitsplit API

## UI Interaction Channels

### Actions
Whatever the UI is, it will be sending "action" objects of the schema `{:type :keyword ...}`, where `:type` is

* `:add-address` add a new address to a given split, needs `:parent` and `:address` at minimum, can autofill/calculate `:percent`
* `:remove-address` all the same as above, but doesn't use `:percent`
* `:new-split` create a new address (or re-use idle removed ones?) from the client to send splits to, can optionally provide `{:children [...]}`, a seq containing a bunch of `:add-address` compatible maps if you want to populate your split right away
* `:remove-split` removes the given `:split` from storage records
* `:edit-address` provide new `:percent` to a given `:address`
* `:list-all` triggers an `:all` response

### Ch-Ch-Ch-Changes
* `:split` provides `:split` (top-level address) and `{:addresses {"addr1" decM, ...}}` with all info needed to re-render a given split. Returned after a `:add-address`, `:remove-address`, `:new-split`, or `:edit-address`
* `:unsplit` provides `:split`, telling you which split to remove from the UI
* `:all` not actually a "change", but provides the `{"split1" {"addr1" number ... } ... }` of all relevant info

Since `:split` provides so much info, it may be worth it to provide some kind of map diffing utils to aid in making minimum changes in nasty OO type UIs.

## A Better Idea
Define all of these things as *synchronous* methods first, then defining this async API will be an extremely simple matter of subscribing to a `:type` and "piping" `actions` "through" the appropriate function, into changes.

### macro for super flexible functions:
`defkw` using a vector of args defines a function that can take either the given arguments in order, a single map of the keyword->value of the args, *or* btc-clj style "spread out" map. Don't actually do this yet, just define methods as taking kwargs (Prismatic's defnk might be handy for this)