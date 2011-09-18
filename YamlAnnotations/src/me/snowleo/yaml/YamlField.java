package me.snowleo.yaml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
public @interface YamlField
{
	String[] comment() default "";
}

