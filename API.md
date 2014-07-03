# The Bitsplit API

## Core API Functions 

Since I'm not quite up to a native UI just yet (probably never will be) and the web service probably isn't using aleph, the async channels version of this is officially put on indefinite hold.

* `add-address` add a new address to a given split, needs `:parent` and `:address` at minimum, can autofill/calculate `:percent`. Returns modified `{"split1" {"address1" percent ...}}`
* `remove-address` all the same as above, but doesn't use `:percent`
* `new-split` create a new address (or re-use idle removed ones?) from the client to send splits to. Returns the address
* `remove-split` removes the given `:split` from storage records, returns the new full set of splits
* `edit-address` provide new `:percent` to a given `:address`, returns the same as `add-address` above
* `list-all` returns all the splits 
* `handle-unspents!` takes a `bitsplit.client` implementation, and `bitsplit.storage` implemenation, and a channel providing unspent transactions in the form `{"address" amount ...}`, and handles all the forwarding