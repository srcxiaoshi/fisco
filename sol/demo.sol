pragma solidity ^0.4.2;
contract demo
{
	string name;
	function demo(){
		name="woshi demo";
	}
	function get() constant returns(string)
	{
		return name;
	}
	function set(string temp_name)
	{
		name=temp_name;
	}
}
