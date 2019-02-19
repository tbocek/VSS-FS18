pragma solidity ^0.4.23;

contract SimpleERC20 {
    function totalSupply() public view returns (uint256);
    function balanceOf(address who) public view returns (uint256);
    function transfer(address to, uint256 value) public returns (bool);
    event Transfer(address indexed from, address indexed to, uint256 value);
}

contract ERC677 {
    event Transfer(address indexed _from, address indexed _to, uint256 _value, bytes _data);

    function transferAndCall(address _to, uint _value, bytes _data) public returns (bool success);
}

contract ERC677Receiver {
    function tokenFallback(address _from, uint _value, bytes _data) public;
}

contract VSSToken2 is SimpleERC20, ERC677 {

    string public constant name = "VSS-TOKEN2";
    string public constant symbol = "VST2";
    uint8 public constant decimals = 18;

    using SafeMath for uint256;
    mapping(address => uint256) balances;
    uint256 totalSupply_;

    constructor() public {
        totalSupply_ = 1000 * (10**18);
        balances[msg.sender] = totalSupply_;
    }

    function totalSupply() public view returns (uint256) {
        return totalSupply_;
    }

    function transfer(address _to, uint256 _value) public returns (bool) {
        require(_to != address(0));
        require(_value <= balances[msg.sender]);

        balances[msg.sender] = balances[msg.sender].sub(_value);
        balances[_to] = balances[_to].add(_value);
        emit Transfer(msg.sender, _to, _value);
        return true;
    }

    function balanceOf(address _owner) public view returns (uint256) {
        return balances[_owner];
    }

    // ERC677 functionality
    function transferAndCall(address _to, uint _value, bytes _data) public returns (bool) {
        require(mintingDone == true);
        require(transfer(_to, _value));

        emit Transfer(msg.sender, _to, _value, _data);

        // call receiver
        if (isContract(_to)) {
            ERC677Receiver receiver = ERC677Receiver(_to);
            receiver.tokenFallback(msg.sender, _value, _data);
        }
        return true;
    }

    function isContract(address _addr) private view returns (bool) {
        uint len;
        assembly {
            len := extcodesize(_addr)
        }
        return len > 0;
    }
}

//using: https://github.com/OpenZeppelin/openzeppelin-solidity/blob/c63b203c1d1800c9a8b5f51f0b444187fdc6c185/contracts/math/SafeMath.sol
library SafeMath {

    /**
    * @dev Multiplies two numbers, throws on overflow.
    */
    function mul(uint256 a, uint256 b) internal pure returns (uint256 c) {
        if (a == 0) {
            return 0;
        }
        c = a * b;
        assert(c / a == b);
        return c;
    }

    /**
    * @dev Integer division of two numbers, truncating the quotient.
    */
    function div(uint256 a, uint256 b) internal pure returns (uint256) {
        // assert(b > 0); // Solidity automatically throws when dividing by 0
        // uint256 c = a / b;
        // assert(a == b * c + a % b); // There is no case in which this doesn't hold
        return a / b;
    }

    /**
    * @dev Subtracts two numbers, throws on overflow (i.e. if subtrahend is greater than minuend).
    */
    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        assert(b <= a);
        return a - b;
    }

    /**
    * @dev Adds two numbers, throws on overflow.
    */
    function add(uint256 a, uint256 b) internal pure returns (uint256 c) {
        c = a + b;
        assert(c >= a);
        return c;
    }
}