/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.orbportability.initialisation.postinit;

public class PostInitialisation
{
    public PostInitialisation()
    {
        System.out.println("PostInitialisation: called");
        _called = true;
        _count++;
    }

    public static long      _count = 0;
    public static boolean   _called = false;
}