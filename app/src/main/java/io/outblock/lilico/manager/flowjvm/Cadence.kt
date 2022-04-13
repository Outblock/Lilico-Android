package io.outblock.lilico.manager.flowjvm

const val CADENCE_TRANSFER_TOKEN = """
    import FungibleToken from 0xFungibleToken
    import FlowToken from 0xFlowToken
    
    transaction(amount: UFix64, to: Address) {
    
        // The Vault resource that holds the tokens that are being transferred
        let sentVault: @FungibleToken.Vault
    
        prepare(signer: AuthAccount) {
    
            // Get a reference to the signer's stored vault
            let vaultRef = signer.borrow<&FlowToken.Vault>(from: /storage/flowTokenVault)
                ?? panic("Could not borrow reference to the owner's Vault!")
    
            // Withdraw tokens from the signer's stored vault
            self.sentVault <- vaultRef.withdraw(amount: amount)
        }
    
        execute {
    
            // Get the recipient's public account object
            let recipient = getAccount(to)
    
            // Get a reference to the recipient's Receiver
            let receiverRef = recipient.getCapability(/public/flowTokenReceiver)
                .borrow<&{FungibleToken.Receiver}>()
                ?? panic("Could not borrow receiver reference to the recipient's Vault")
    
            // Deposit the withdrawn tokens in the recipient's receiver
            receiverRef.deposit(from: <-self.sentVault)
        }
    }
"""

// check coin token is contains in wallet
const val CADENCE_CHECK_TOKEN_IS_ENABLED = """
    import FungibleToken from 0xFungibleToken
    import <Token> from <TokenAddress>
    
    pub fun main(address: Address) : Bool {
       let receiver: Bool = getAccount(address)
       .getCapability<&<Token>.Vault{FungibleToken.Receiver}>(<TokenReceiverPath>)
       .check()
       let balance: Bool = getAccount(address)
        .getCapability<&<Token>.Vault{FungibleToken.Balance}>(<TokenBalancePath>)
        .check()
        return receiver && balance
     }
"""

// enable new coin token for wallet
const val CADENCE_ADD_TOKEN = """
    import FungibleToken from 0xFungibleToken
    import <Token> from <TokenAddress>
    
    transaction {
    
      prepare(signer: AuthAccount) {
    
        if(signer.borrow<&<Token>.Vault>(from: <TokenStoragePath>) != nil) {
          return
        }
        
        signer.save(<-<Token>.createEmptyVault(), to: <TokenStoragePath>)
    
        signer.link<&<Token>.Vault{FungibleToken.Receiver}>(
          <TokenReceiverPath>,
          target: <TokenStoragePath>
        )
    
        signer.link<&<Token>.Vault{FungibleToken.Balance}>(
          <TokenBalancePath>,
          target: <TokenStoragePath>
        )
      }
    }
"""

const val CADENCE_GET_BALANCE = """
    import FungibleToken from 0xFungibleToken
    import <Token> from <TokenAddress>

    pub fun main(address: Address): UFix64 {
      let account = getAccount(address)

      let vaultRef = account
        .getCapability(<TokenBalancePath>)
        .borrow<&<Token>.Vault{FungibleToken.Balance}>()
        ?? panic("Could not borrow Balance capability")

      return vaultRef.balance
 }
"""

const val CADENCE_QUERY_ADDRESS_BY_DOMAIN_FLOWNS = """
  import Flowns from 0xFlowns
  import Domains from 0xDomains
  pub fun main(name: String, root: String) : Address? {
    let prefix = "0x"
    let rootHahsh = Flowns.hash(node: "", lable: root)
    let namehash = prefix.concat(Flowns.hash(node: rootHahsh, lable: name))
    var address = Domains.getRecords(namehash)
    return address
  }
"""

const val CADENCE_QUERY_DOMAIN_BY_ADDRESS_FLOWNS = """
  import Domains from 0xDomains
  // address: Flow address
  pub fun main(address: Address): [Domains.DomainDetail] {
    let account = getAccount(address)
    let collectionCap = account.getCapability<&{Domains.CollectionPublic}>(Domains.CollectionPublicPath)
    let collection = collectionCap.borrow()!
    let domains:[Domains.DomainDetail] = []
    let ids = collection.getIDs()
    for id in ids {
      let domain = collection.borrowDomain(id: id)
      let detail = domain.getDetail()
      domains.append(detail)
    }
    return domains
  }
"""

const val CADENCE_QUERY_ADDRESS_BY_DOMAIN_FIND = """
  import FIND from 0xFind
  //Check the status of a fin user
  pub fun main(name: String) : Address? {
      let status=FIND.status(name)
      return status.owner
  }
"""

const val CADENCE_QUERY_DOMAIN_BY_ADDRESS_FIND = """
  import FIND from 0xFind
  pub fun main(address: Address) : String?{
    return FIND.reverseLookup(address)
  }
"""

const val CADENCE_NFT_CHECK_ENABLED = """
    import NonFungibleToken from 0xNonFungibleToke
    import <NFT> from <NFTAddress>
    
    // This transaction is for transferring and NFT from
    // one account to another
    
    pub fun check<Token>Vault(address: Address) : Bool {
        let account = getAccount(address)
    
        let vaultRef = account
        .getCapability<&{NonFungibleToken.CollectionPublic}>(<TokenCollectionPublicPath>)
        .check()
    
        return vaultRef
    }
"""

const val CADENCE_NFT_ENABLE = """
    import NonFungibleToken from 0xNonFungibleToken
    import <NFT> from <NFTAddress>
    
    transaction {
      prepare(signer: AuthAccount) {
          // if the account doesn't already have a collection
          if signer.borrow<&<NFT>.Collection>(from: <CollectionStoragePath>) == nil {
    
              // create a new empty collection
              let collection <- <NFT>.createEmptyCollection()
              
              // save it to the account
              signer.save(<-collection, to: <CollectionStoragePath>)
    
              // create a public capability for the collection
              signer.link<&<NFT>.Collection{NonFungibleToken.CollectionPublic, <CollectionPublic>}>(<CollectionPublicPath>, target: <CollectionStoragePath>)
          }
      }
    }
"""

const val CADENCE_NFT_TRANSFER = """
    import NonFungibleToken from 0xNonFungibleToken
    import <NFT> from <NFTAddress>
    
    // This transaction is for transferring and NFT from
    // one account to another
    
    transaction(recipient: Address, withdrawID: UInt64) {
    
      prepare(signer: AuthAccount) {
          // get the recipients public account object
          let recipient = getAccount(recipient)
    
          // borrow a reference to the signer's NFT collection
          let collectionRef = signer
              .borrow<&NonFungibleToken.Collection>(from: <CollectionStoragePath>)
              ?? panic("Could not borrow a reference to the owner's collection")
    
          // borrow a public reference to the receivers collection
          let depositRef = recipient
              .getCapability(<CollectionPublicPath>)
              .borrow<&{<CollectionPublic>}>()
              ?? panic("Could not borrow a reference to the receiver's collection")
    
          // withdraw the NFT from the owner's collection
          let nft <- collectionRef.withdraw(withdrawID: withdrawID)
    
          // Deposit the NFT in the recipient's collection
          depositRef.deposit(token: <-nft)
      }
    }
"""