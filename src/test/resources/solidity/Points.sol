pragma solidity >=0.4.24 <0.6.11;

contract Points {
    // 存储用户的积分
    mapping(address => uint256) public points;

    /**
     * 添加积分
     * @param _points 要添加的积分数量
     */
    function addPoints(uint256 _points) public {
        points[msg.sender] += _points;
    }

    /**
     * 减少积分
     * @param _points 要减少的积分数量
     */
    function subtractPoints(uint256 _points) public {
        require(points[msg.sender] >= _points, "Not enough points to subtract");
        points[msg.sender] -= _points;
    }

    /**
     * 查询积分
     * @return 当前用户的积分数量
     */
    function getPoints() public view returns (uint256) {
        return points[msg.sender];
    }
}