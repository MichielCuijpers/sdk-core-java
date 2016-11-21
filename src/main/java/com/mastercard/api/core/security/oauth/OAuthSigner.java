package com.mastercard.api.core.security.oauth;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.OAuthMessageSigner;
import oauth.signpost.signature.SignatureBaseString;

import java.security.PrivateKey;
import java.security.Signature;


/**
 * Created with IntelliJ IDEA.
 * User: eamondoyle
 * Date: 17/07/2014
 * Time: 14:38
 */
public class OAuthSigner extends OAuthMessageSigner {

    private PrivateKey signingPrivateKey;

    public OAuthSigner(PrivateKey signingPrivateKey) {
        this.signingPrivateKey = signingPrivateKey;
    }

    @Override
    public String sign(HttpRequest request, HttpParameters requestParams) throws OAuthMessageSignerException {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(signingPrivateKey);

            String sbs = new SignatureBaseString(request, requestParams).generate();
            OAuth.debugOut("SBS", sbs);
            byte[] text = sbs.getBytes(OAuth.ENCODING);

            signer.update(text);

            return base64Encode(signer.sign());
        }
        catch (Exception e) {
            throw new OAuthMessageSignerException(e);
        }
    }

    @Override
    public String getSignatureMethod() {
        return "RSA-SHA256";
    }
}
