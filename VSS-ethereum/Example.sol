pragma solidity ^0.4.23;

contract Test123 {
    struct Account {
        string name;
        uint256 value;
    }
    
    Account account;
    
    address owner;
    
    event Message(string);
    
    constructor () public {
        owner = msg.sender;
    }
    
    //an example of a function that modifies the state
    function setAccount(string name, uint256 value) public {
        require(owner == msg.sender);
        account = Account(name, value);
        emit Message("account set");
    }
    
    //an example of a view/constant function, reads the state
    function getAccountName() public constant returns (string) {
        return account.name;
    }
    
    //an example of a view/constant function, reads the state
    function getAccountValue() public view returns (uint256) {
        return account.value;
    }
    
    //an example of a pure function, does not read or write the state
    function calc(uint256 val) public pure returns (uint256) {
        return val + val;
    }
}
