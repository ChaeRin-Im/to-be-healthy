package com.tobe.healthy.common;

public interface EnumMapperTypeExt extends EnumMapperType{
	<T extends Enum<T> & EnumMapperType> T getParentCode();
}
