package io.outblock.lilico.manager.flowjvm

const val CADENCE_TRANSFER_TOKEN = """
  import FungibleToken from 0xFungibleToken
  import Domains from 0xFlowns
  import <Token> from <TokenAddress>
  transaction(amount: UFix64, recipient: Address) {
    let senderRef: &{FungibleToken.Receiver}
    // The Vault resource that holds the tokens that are being transfered
    let sentVault: @FungibleToken.Vault
    let sender: Address
    prepare(signer: AuthAccount) {
      // Get a reference to the signer's stored vault
      let vaultRef = signer.borrow<&<Token>.Vault>(from: <TokenStoragePath>)
        ?? panic("Could not borrow reference to the owner's Vault!")
      self.senderRef = signer.getCapability(<TokenReceiverPath>)
        .borrow<&{FungibleToken.Receiver}>()!
      self.sender = vaultRef.owner!.address
      // Withdraw tokens from the signer's stored vault
      self.sentVault <- vaultRef.withdraw(amount: amount)
    }
    execute {
      // Get the recipient's public account object
      let recipientAccount = getAccount(recipient)
      // Get a reference to the recipient's Receiver
      let receiverRef = recipientAccount.getCapability(<TokenReceiverPath>)
        .borrow<&{FungibleToken.Receiver}>()
      
      if receiverRef == nil {
          let collectionCap = recipientAccount.getCapability<&{Domains.CollectionPublic}>(Domains.CollectionPublicPath)
          let collection = collectionCap.borrow()!
          var defaultDomain: &{Domains.DomainPublic}? = nil
          let ids = collection.getIDs()
          if ids.length == 0 {
              panic("Recipient have no domain ")
          }
          
          defaultDomain = collection.borrowDomain(id: ids[0])!
              // check defualt domain 
          for id in ids {
            let domain = collection.borrowDomain(id: id)!
            let isDefault = domain.getText(key: "isDefault")
            if isDefault == "true" {
              defaultDomain = domain
            }
          }
          // Deposit the withdrawn tokens in the recipient's domain inbox
          defaultDomain!.depositVault(from: <- self.sentVault, senderRef: self.senderRef)
      } else {
          // Deposit the withdrawn tokens in the recipient's receiver
          receiverRef!.deposit(from: <- self.sentVault)
      }
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
    import NonFungibleToken from 0xNonFungibleToken
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
    
    pub fun main(address: Address) : Bool {
        return check<Token>Vault(address: address)
    }
"""

const val CADENCE_NFT_ENABLE = """
    import NonFungibleToken from 0xNonFungibleToken
    import MetadataViews from 0xMetadataViews
    import <NFT> from <NFTAddress>

    transaction {

      prepare(signer: AuthAccount) {
        if signer.borrow<&<NFT>.Collection>(from: <CollectionStoragePath>) == nil {
          let collection <- <NFT>.createEmptyCollection()
          signer.save(<-collection, to: <CollectionStoragePath>)
        }
        if (signer.getCapability<&<CollectionPublicType>>(<CollectionPublicPath>).borrow() == nil) {
          signer.unlink(<CollectionPublicPath>)
          signer.link<&<CollectionPublicType>>(<CollectionPublicPath>, target: <CollectionStoragePath>)
        }
      }
    }
"""

const val CADENCE_NFT_TRANSFER = """
  import NonFungibleToken from 0xNonFungibleToken
  import Domains from 0xDomains
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
      let senderRef = signer
        .getCapability(<CollectionPublicPath>)
        .borrow<&{NonFungibleToken.CollectionPublic}>()
      // borrow a public reference to the receivers collection
      let recipientRef = recipient
        .getCapability(<CollectionPublicPath>)
        .borrow<&{<CollectionPublic>}>()
      
      if recipientRef == nil {
        let collectionCap = recipient.getCapability<&{Domains.CollectionPublic}>(Domains.CollectionPublicPath)
        let collection = collectionCap.borrow()!
        var defaultDomain: &{Domains.DomainPublic}? = nil
      
        let ids = collection.getIDs()
        if ids.length == 0 {
          panic("Recipient have no domain ")
        }
        
        // check defualt domain 
        defaultDomain = collection.borrowDomain(id: ids[0])!
        // check defualt domain 
        for id in ids {
          let domain = collection.borrowDomain(id: id)!
          let isDefault = domain.getText(key: "isDefault")
          if isDefault == "true" {
            defaultDomain = domain
          }
        }
        let typeKey = collectionRef.getType().identifier
        // withdraw the NFT from the owner's collection
        let nft <- collectionRef.withdraw(withdrawID: withdrawID)
        if defaultDomain!.checkCollection(key: typeKey) == false {
          let collection <- <NFT>.createEmptyCollection()
          defaultDomain!.addCollection(collection: <- collection)
        }
        defaultDomain!.depositNFT(key: typeKey, token: <- nft, senderRef: senderRef )
      } else {
        // withdraw the NFT from the owner's collection
        let nft <- collectionRef.withdraw(withdrawID: withdrawID)
        // Deposit the NFT in the recipient's collection
        recipientRef!.deposit(token: <-nft)
      }
    }
  }
"""

const val CADENCE_CLAIM_INBOX_TOKEN = """
  import Domains from 0xDomains
  import FungibleToken from 0xFungibleToken
  import Flowns from 0xFlowns
  import <Token> from <TokenAddress>
  transaction(name: String, root:String, key:String, amount: UFix64) {
    var domain: &{Domains.DomainPrivate}
    var vaultRef: &<Token>.Vault
    prepare(account: AuthAccount) {
      let prefix = "0x"
      let rootHahsh = Flowns.hash(node: "", lable: root)
      let nameHash = prefix.concat(Flowns.hash(node: rootHahsh, lable: name))
      let collectionCap = account.getCapability<&{Domains.CollectionPublic}>(Domains.CollectionPublicPath) 
      let collection = collectionCap.borrow()!
      var domain: &{Domains.DomainPrivate}? = nil
      let collectionPrivate = account.borrow<&{Domains.CollectionPrivate}>(from: Domains.CollectionStoragePath) ?? panic("Could not find your domain collection cap")
      
      let ids = collection.getIDs()
      let id = Domains.getDomainId(nameHash)
      if id != nil && !Domains.isDeprecated(nameHash: nameHash, domainId: id!) {
        domain = collectionPrivate.borrowDomainPrivate(id!)
      }
      self.domain = domain!
      let vaultRef = account.borrow<&<Token>.Vault>(from: <TokenStoragePath>)
      if vaultRef == nil {
        account.save(<- <Token>.createEmptyVault(), to: <TokenStoragePath>)
        account.link<&<Token>.Vault{FungibleToken.Receiver}>(
          <TokenReceiverPath>,
          target: <TokenStoragePath>
        )
        account.link<&<Token>.Vault{FungibleToken.Balance}>(
          <TokenBalancePath>,
          target: <TokenStoragePath>
        )
        self.vaultRef = account.borrow<&<Token>.Vault>(from: <TokenStoragePath>)
      ?? panic("Could not borrow reference to the owner's Vault!")
      } else {
        self.vaultRef = vaultRef!
      }
    }
    execute {
      self.vaultRef.deposit(from: <- self.domain.withdrawVault(key: key, amount: amount))
    }
  }
"""

const val CADENCE_CLAIM_INBOX_NFT = """
  import Domains from 0xDomains
  import Flowns from 0xFlowns
  import NonFungibleToken from 0xNonFungibleToken
  import MetadataViews from 0xMetadataViews
  import <NFT> from <NFTAddress>

  // key will be 'A.f8d6e0586b0a20c7.Domains.Collection' of a NFT collection
  transaction(name: String, root: String, key: String, itemId: UInt64) {
    var domain: &{Domains.DomainPrivate}
    var collectionRef: &<NFT>.Collection
    prepare(account: AuthAccount) {
      let prefix = "0x"
      let rootHahsh = Flowns.hash(node: "", lable: root)
      let nameHash = prefix.concat(Flowns.hash(node: rootHahsh, lable: name))
      var domain: &{Domains.DomainPrivate}? = nil
      let collectionPrivate = account.borrow<&{Domains.CollectionPrivate}>(from: Domains.CollectionStoragePath) ?? panic("Could not find your domain collection cap")

      let id = Domains.getDomainId(nameHash)
      if id !=nil {
        domain = collectionPrivate.borrowDomainPrivate(id!)
      }
      self.domain = domain!

      let collectionRef = account.borrow<&<NFT>.Collection>(from: <CollectionStoragePath>)
      if collectionRef == nil {
        account.save(<- <NFT>.createEmptyCollection(), to: <CollectionStoragePath>)
        account.link<&<CollectionPublicType>>(<CollectionPublicPath>, target: <CollectionStoragePath>)
        self.collectionRef = account.borrow<&<NFT>.Collection>(from: <CollectionStoragePath>)?? panic("Can not borrow collection")
      } else {
        self.collectionRef = collectionRef!
      }
    
    }
    execute {
      self.collectionRef.deposit(token: <- self.domain.withdrawNFT(key: key, itemId: itemId))
    }
  }
"""

// want use how many token to swap other token
const val CADENCE_SWAP_EXACT_TOKENS_TO_OTHER_TOKENS =
    """import Token1Name from Token1Addr
    import FungibleToken from 0x9a0766d93b6608b7
    import SwapRouter from 0x2f8af5ed05bbde0d
    import SwapError from 0xddb929038d45d4b3
    transaction(
        tokenKeyFlatSplitPath: [String],
        amountInSplit: [UFix64],
        amountOutMin: UFix64,
        deadline: UFix64,
        tokenInVaultPath: StoragePath,
        tokenOutVaultPath: StoragePath,
        tokenOutReceiverPath: PublicPath,
        tokenOutBalancePath: PublicPath,
    ) {
        prepare(userAccount: AuthAccount) {
            assert(deadline >= getCurrentBlock().timestamp, message:
                SwapError.ErrorEncode(
                    msg: "EXPIRED",
                    err: SwapError.ErrorCode.EXPIRED
                )
            )
            let len = tokenKeyFlatSplitPath.length
            let tokenInKey = tokenKeyFlatSplitPath[0]
            let tokenOutKey = tokenKeyFlatSplitPath[len-1]
            var tokenOutAmountTotal = 0.0
            var tokenOutReceiverRef = userAccount.borrow<&FungibleToken.Vault>(from: tokenOutVaultPath)
            if tokenOutReceiverRef == nil {
                userAccount.save(<- Token1Name.createEmptyVault(), to: tokenOutVaultPath)
                userAccount.link<&Token1Name.Vault{FungibleToken.Receiver}>(tokenOutReceiverPath, target: tokenOutVaultPath)
                userAccount.link<&Token1Name.Vault{FungibleToken.Balance}>(tokenOutBalancePath, target: tokenOutVaultPath)
                tokenOutReceiverRef = userAccount.borrow<&FungibleToken.Vault>(from: tokenOutVaultPath)
            }
            var pathIndex = 0
            var i = 0
            var path: [String] = []
            while(i < len) {
                var curTokenKey = tokenKeyFlatSplitPath[i]
                path.append(curTokenKey)
                if (curTokenKey == tokenOutKey) {
                    log(path)
                    let tokenInAmount = amountInSplit[pathIndex]
                    let tokenInVault <- userAccount.borrow<&FungibleToken.Vault>(from: tokenInVaultPath)!.withdraw(amount: tokenInAmount)
                    let tokenOutVault <- SwapRouter.swapWithPath(vaultIn: <- tokenInVault, tokenKeyPath: path, exactAmounts: nil)
                    tokenOutAmountTotal = tokenOutAmountTotal + tokenOutVault.balance
                    tokenOutReceiverRef!.deposit(from: <- tokenOutVault)
                    path = []
                    pathIndex = pathIndex + 1
                }
                i = i + 1
            }
            assert(tokenOutAmountTotal >= amountOutMin, message:
                SwapError.ErrorEncode(
                    msg: "SLIPPAGE_OFFSET_TOO_LARGE expect min ".concat(amountOutMin.toString()).concat(" got ").concat(tokenOutAmountTotal.toString()),
                    err: SwapError.ErrorCode.SLIPPAGE_OFFSET_TOO_LARGE
                )
            )
        }
    }"""

// want swap how many other token
const val CADENCE_SWAP_TOKENS_FROM_EXACT_TOKENS =
    """import Token1Name from Token1Addr
    import FungibleToken from 0x9a0766d93b6608b7
    import SwapRouter from 0x2f8af5ed05bbde0d
    import SwapError from 0xddb929038d45d4b3
    transaction(
        tokenKeyFlatSplitPath: [String],
        amountOutSplit: [UFix64],
        amountInMax: UFix64,
        deadline: UFix64,
        tokenInVaultPath: StoragePath,
        tokenOutVaultPath: StoragePath,
        tokenOutReceiverPath: PublicPath,
        tokenOutBalancePath: PublicPath,
    ) {
        prepare(userAccount: AuthAccount) {
            assert( deadline >= getCurrentBlock().timestamp, message:
                SwapError.ErrorEncode(
                    msg: "EXPIRED",
                    err: SwapError.ErrorCode.EXPIRED
                )
            )
            let len = tokenKeyFlatSplitPath.length
            let tokenInKey = tokenKeyFlatSplitPath[0]
            let tokenOutKey = tokenKeyFlatSplitPath[len-1]
            var tokenOutAmountTotal = 0.0
            var tokenOutReceiverRef = userAccount.borrow<&FungibleToken.Vault>(from: tokenOutVaultPath)
            if tokenOutReceiverRef == nil {
                userAccount.save(<- Token1Name.createEmptyVault(), to: tokenOutVaultPath)
                userAccount.link<&Token1Name.Vault{FungibleToken.Receiver}>(tokenOutReceiverPath, target: tokenOutVaultPath)
                userAccount.link<&Token1Name.Vault{FungibleToken.Balance}>(tokenOutBalancePath, target: tokenOutVaultPath)
                tokenOutReceiverRef = userAccount.borrow<&FungibleToken.Vault>(from: tokenOutVaultPath)
            }
            var pathIndex = 0
            var i = 0
            var path: [String] = []
            var amountInTotal = 0.0
            while(i < len) {
                var curTokenKey = tokenKeyFlatSplitPath[i]
                path.append(curTokenKey)
                if (curTokenKey == tokenOutKey) {
                    log(path)
                    let tokenOutExpectAmount = amountOutSplit[pathIndex]
                    let amounts = SwapRouter.getAmountsIn(amountOut: tokenOutExpectAmount, tokenKeyPath: path)
                    let tokenInAmount = amounts[0]
                    amountInTotal = amountInTotal + tokenInAmount
                    let tokenInVault <- userAccount.borrow<&FungibleToken.Vault>(from: tokenInVaultPath)!.withdraw(amount: tokenInAmount)
                    let tokenOutVault <- SwapRouter.swapWithPath(vaultIn: <- tokenInVault, tokenKeyPath: path, exactAmounts: amounts)
                    tokenOutAmountTotal = tokenOutAmountTotal + tokenOutVault.balance
                    tokenOutReceiverRef!.deposit(from: <- tokenOutVault)
                    path = []
                    pathIndex = pathIndex + 1
                }
                i = i + 1
            }
            assert(amountInTotal <= amountInMax, message:
                SwapError.ErrorEncode(
                    msg: "SLIPPAGE_OFFSET_TOO_LARGE",
                    err: SwapError.ErrorCode.SLIPPAGE_OFFSET_TOO_LARGE
                )
            )
        }
    }"""

const val CADENCE_CREATE_STAKE_DELEGATOR_ID = """
    import FlowStakingCollection from 0xStakingCollection

    /// Registers a delegator in the staking collection resource
    /// for the specified nodeID and the amount of tokens to commit
    
    transaction(id: String, amount: UFix64) {
        
        let stakingCollectionRef: &FlowStakingCollection.StakingCollection
    
        prepare(account: AuthAccount) {
            self.stakingCollectionRef = account.borrow<&FlowStakingCollection.StakingCollection>(from: FlowStakingCollection.StakingCollectionStoragePath)
                ?? panic("Could not borrow ref to StakingCollection")
        }
    
        execute {
            self.stakingCollectionRef.registerDelegator(nodeID: id, amount: amount)      
        }
    }
"""


const val CADENCE_STAKE_FLOW = """
    import FlowStakingCollection from 0xFlowStakingCollection

    /// Commits new tokens to stake for the specified node or delegator in the staking collection
    /// The tokens from the locked vault are used first, if it exists
    /// followed by the tokens from the unlocked vault
    
    transaction(nodeID: String, delegatorID: UInt32?, amount: UFix64) {
        
        let stakingCollectionRef: &FlowStakingCollection.StakingCollection
    
        prepare(account: AuthAccount) {
            self.stakingCollectionRef = account.borrow<&FlowStakingCollection.StakingCollection>(from: FlowStakingCollection.StakingCollectionStoragePath)
                ?? panic("Could not borrow ref to StakingCollection")
        }
    
        execute {
            self.stakingCollectionRef.stakeNewTokens(nodeID: nodeID, delegatorID: delegatorID, amount: amount)
        }
    }
"""

const val CADENCE_QUERY_STAKE_INFO = """
    import LockedTokens from 0x8d0e87b65159ae63
    import FlowIDTableStaking from 0x8624b52f9ddcd04a
    import FlowStakingCollection from 0x8d0e87b65159ae63
    
    pub fun main(account: Address): [FlowIDTableStaking.DelegatorInfo] {
    
        let stakingCollectionRef = getAccount(account)
            .getCapability<&{FlowStakingCollection.StakingCollectionPublic}>(FlowStakingCollection.StakingCollectionPublicPath)
            .borrow()
            ?? panic("cannot borrow reference to acct.StakingCollection")
    
        return stakingCollectionRef.getAllDelegatorInfo()
    }
"""

const val CADENCE_GET_STAKE_APY_BY_WEEK = """
    import FlowIDTableStaking from 0x8624b52f9ddcd04a

    pub fun main(): UFix64 {
        let apr = FlowIDTableStaking.getEpochTokenPayout() / FlowIDTableStaking.getTotalStaked() * 54.0 * (1.0 - FlowIDTableStaking.getRewardCutPercentage())
        return apr
    }
"""

const val CADENCE_CHECK_IS_STAKING_SETUP = """
    import FlowStakingCollection from 0x8d0e87b65159ae63

    /// Determines if an account is set up with a Staking Collection
    
    pub fun main(address: Address): Bool {
        return FlowStakingCollection.doesAccountHaveStakingCollection(address: address)
    }
"""

const val CADENCE_SETUP_STAKING = """
    import FungibleToken from 0xFungibleToken
    import FlowToken from 0xFlowToken
    import FlowIDTableStaking from 0xFlowIDTableStaking
    import LockedTokens from 0xLockedTokens
    import FlowStakingCollection from 0xFlowStakingCollection
    
    /// This transaction sets up an account to use a staking collection
    /// It will work regardless of whether they have a regular account, a two-account locked tokens setup,
    /// or staking objects stored in the unlocked account
    
    transaction {
        prepare(signer: AuthAccount) {
    
            // If there isn't already a staking collection
            if signer.borrow<&FlowStakingCollection.StakingCollection>(from: FlowStakingCollection.StakingCollectionStoragePath) == nil {
    
                // Create private capabilities for the token holder and unlocked vault
                let lockedHolder = signer.link<&LockedTokens.TokenHolder>(/private/flowTokenHolder, target: LockedTokens.TokenHolderStoragePath)!
                let flowToken = signer.link<&FlowToken.Vault>(/private/flowTokenVault, target: /storage/flowTokenVault)!
                
                // Create a new Staking Collection and put it in storage
                if lockedHolder.check() {
                    signer.save(<-FlowStakingCollection.createStakingCollection(unlockedVault: flowToken, tokenHolder: lockedHolder), to: FlowStakingCollection.StakingCollectionStoragePath)
                } else {
                    signer.save(<-FlowStakingCollection.createStakingCollection(unlockedVault: flowToken, tokenHolder: nil), to: FlowStakingCollection.StakingCollectionStoragePath)
                }
    
                // Create a public link to the staking collection
                signer.link<&FlowStakingCollection.StakingCollection{FlowStakingCollection.StakingCollectionPublic}>(
                    FlowStakingCollection.StakingCollectionPublicPath,
                    target: FlowStakingCollection.StakingCollectionStoragePath
                )
            }
    
            // borrow a reference to the staking collection
            let collectionRef = signer.borrow<&FlowStakingCollection.StakingCollection>(from: FlowStakingCollection.StakingCollectionStoragePath)
                ?? panic("Could not borrow staking collection reference")
    
            // If there is a node staker object in the account, put it in the staking collection
            if signer.borrow<&FlowIDTableStaking.NodeStaker>(from: FlowIDTableStaking.NodeStakerStoragePath) != nil {
                let node <- signer.load<@FlowIDTableStaking.NodeStaker>(from: FlowIDTableStaking.NodeStakerStoragePath)!
                collectionRef.addNodeObject(<-node, machineAccountInfo: nil)
            }
    
            // If there is a delegator object in the account, put it in the staking collection
            if signer.borrow<&FlowIDTableStaking.NodeDelegator>(from: FlowIDTableStaking.DelegatorStoragePath) != nil {
                let delegator <- signer.load<@FlowIDTableStaking.NodeDelegator>(from: FlowIDTableStaking.DelegatorStoragePath)!
                collectionRef.addDelegatorObject(<-delegator)
            }
        }
    }
"""

const val CADENCE_GET_STAKE_APY_BY_YEAR = """
    import FlowIDTableStaking from 0x8624b52f9ddcd04a
    
    pub fun main(): UFix64 {
        let apr = FlowIDTableStaking.getEpochTokenPayout() / FlowIDTableStaking.getTotalStaked() / 7.0 * 365.0 * (1.0 - FlowIDTableStaking.getRewardCutPercentage())
        return apr
    }
"""

const val CADENCE_CHECK_STAKING_ENABLED = """
    import FlowIDTableStaking from 0x8624b52f9ddcd04a

    pub fun main():Bool {
      return FlowIDTableStaking.stakingEnabled()
    }
"""

const val CADENCE_GET_DELEGATOR_INFO = """
    import FlowStakingCollection from 0x8d0e87b65159ae63
    import FlowIDTableStaking from 0x8624b52f9ddcd04a
    import LockedTokens from 0x8d0e87b65159ae63
    
    pub struct DelegateInfo {
        pub let delegatorID: UInt32
        pub let nodeID: String
        pub let tokensCommitted: UFix64
        pub let tokensStaked: UFix64
        pub let tokensUnstaking: UFix64
        pub let tokensRewarded: UFix64
        pub let tokensUnstaked: UFix64
        pub let tokensRequestedToUnstake: UFix64
    
        // Projected Values
    
        pub let id: String
        pub let role: UInt8
        pub let unstakableTokens: UFix64
        pub let delegatedNodeInfo: FlowIDTableStaking.NodeInfo
        pub let restakableUnstakedTokens: UFix64
    
        init(delegatorInfo: FlowIDTableStaking.DelegatorInfo) {
            self.delegatorID = delegatorInfo.id
            self.nodeID = delegatorInfo.nodeID
            self.tokensCommitted = delegatorInfo.tokensCommitted
            self.tokensStaked = delegatorInfo.tokensStaked
            self.tokensUnstaking = delegatorInfo.tokensUnstaking
            self.tokensUnstaked = delegatorInfo.tokensUnstaked
            self.tokensRewarded = delegatorInfo.tokensRewarded
            self.tokensRequestedToUnstake = delegatorInfo.tokensRequestedToUnstake
    
            // Projected Values
            let nodeInfo = FlowIDTableStaking.NodeInfo(nodeID: delegatorInfo.nodeID)
            self.delegatedNodeInfo = nodeInfo
            self.id = nodeInfo.id
            self.role = nodeInfo.role
            self.unstakableTokens = self.tokensStaked + self.tokensCommitted
            self.restakableUnstakedTokens = self.tokensUnstaked + self.tokensRequestedToUnstake
        }
    }
    
    pub fun main(account: Address): {String: {UInt32: DelegateInfo}}? {
        let doesAccountHaveStakingCollection = FlowStakingCollection.doesAccountHaveStakingCollection(address: account)
        if (!doesAccountHaveStakingCollection) {
            return nil
        }
    
        let delegatorIDs: [FlowStakingCollection.DelegatorIDs] = FlowStakingCollection.getDelegatorIDs(address: account)
    
        let formattedDelegatorInfo: {String: {UInt32: DelegateInfo}} = {}
    
        for delegatorID in delegatorIDs {
            if let _formattedDelegatorInfo = formattedDelegatorInfo[delegatorID.delegatorNodeID] {
                let delegatorInfo: FlowIDTableStaking.DelegatorInfo = FlowIDTableStaking.DelegatorInfo(nodeID: delegatorID.delegatorNodeID, delegatorID: delegatorID.delegatorID)
                _formattedDelegatorInfo[delegatorID.delegatorID] = DelegateInfo(delegatorInfo: delegatorInfo)
            } else {
                let delegatorInfo: FlowIDTableStaking.DelegatorInfo = FlowIDTableStaking.DelegatorInfo(nodeID: delegatorID.delegatorNodeID, delegatorID: delegatorID.delegatorID)
                formattedDelegatorInfo[delegatorID.delegatorNodeID] = { delegatorID.delegatorID: DelegateInfo(delegatorInfo: delegatorInfo)}
            }
        }
    
        return formattedDelegatorInfo
    }
"""