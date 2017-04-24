package com.mastercard.api.core.security.oauth;

import oauth.signpost.basic.DefaultOAuthConsumer;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by e049519 on 4/19/17.
 */
public class CustomOAuthConsumer extends DefaultOAuthConsumer {

    final private static SecureRandom random = new SecureRandom();

    public CustomOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected String generateNonce() {
        return new BigInteger(130, random).toString(32);
    }

}
