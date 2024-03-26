pragma solidity >=0.6.0 <0.8.20;

contract CharitySplitter {
    address public owner;

    constructor(address _owner) public {
        require(_owner != address(0), "no-owner-provided");
        owner = _owner;
    }
}

contract CharitySplitterFactory {
    mapping(address => CharitySplitter) public charitySplitters;
    uint256 public errorCount;
    event ErrorHandled(string reason);
    event ErrorNotHandled(bytes reason);

    function createCharitySplitter(address charityOwner) public {
        try new CharitySplitter(charityOwner) returns (
            CharitySplitter newCharitySplitter
        ) {
            charitySplitters[msg.sender] = newCharitySplitter;
        } catch {
            errorCount++;
        }
    }
}
