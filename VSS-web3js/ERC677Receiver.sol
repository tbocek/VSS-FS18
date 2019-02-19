pragma solidity ^0.4.23;

contract ERC677Receiver {
    function tokenFallback(address _from, uint _value, bytes _data) public;
}

contract Utility is ERC677Receiver {

    mapping(address => uint256) history;

    function tokenFallback(address _from, uint _value, bytes _data) public {
        history[_from] = _value;
    }

    function historyOf(address _owner) public view returns (uint256) {
        return history[_owner];
    }
}