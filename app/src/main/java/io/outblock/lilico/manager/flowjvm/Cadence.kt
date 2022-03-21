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