/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.wstf.proxy;


/**
 * @author kevin
 */
public class ProxyURIRewriting
{
    /**
     * The base URI of the proxy.
     */
    private static String PROXY_URI ;
    
    /**
     * Set the proxy URI.
     * @param proxyURI The proxy URI.
     */
    public static synchronized void setProxyURI(final String proxyURI)
    {
        PROXY_URI = proxyURI ;
    }
    
    /**
     * Get the proxy URI.
     * @return The proxy URI.
     */
    public static synchronized String getProxyURI()
    {
        return PROXY_URI ;
    }
    
    /**
     * Rewrite the URI.
     * @param conversationIdentifier The conversation identifier.
     * @param uri The uri to be rewritten.
     * @return The replacement URI.
     */
    public static String rewriteURI(final String conversationIdentifier, final String uri)
    {
        final String proxyURI = getProxyURI() ;
        
        if (uri != null)
        {
            if (uri.startsWith(proxyURI))
            {
                final int separatorIndex = uri.indexOf('/', proxyURI.length()+1) ;
                final String remainder = uri.substring(separatorIndex+1) ;
                return decodeURI(remainder) ;
            }
            else if (!uri.startsWith("http://www.w3.org/"))
            {
                return proxyURI + "/" + conversationIdentifier + "/" + encodeURI(uri) ;
            }
        }
        return uri ;
    }
    
    /**
     * Decode a URI that has been encoded.
     * @param uri The encoded URI.
     * @return The decoded URI.
     */
    public static String decodeURI(final String uri)
    {
        if (uri == null)
        {
            return null ;
        }
        final StringBuffer result = new StringBuffer() ;
        final int length = uri.length() ;
        int separatorCount = 0 ;
        boolean seenBracket = false;
        for(int count = 0 ; count < length ; count++)
        {
            final char ch = uri.charAt(count) ;
            if (separatorCount < 3)
            {
                if (ch == '-')
                {
                    result.append('/') ;
                    separatorCount++ ;
                    continue ;
                }
                else if (ch == '_')
                {
                    result.append(':') ;
                    continue ;
                }
                else if (ch == '~')
                {
                    result.append(seenBracket ? ']' : '[') ;
                    seenBracket = !seenBracket;
                    continue ;
                }
                result.append(ch) ;
            }
            else
            {
                result.append(ch) ;
            }
        }
        return result.toString() ;
    }
    
    /**
     * Encode a URI.
     * @param uri The URI.
     * @return The encoded URI.
     */
    public static String encodeURI(final String uri)
    {
        if (uri == null)
        {
            return "" ;
        }
        
        final StringBuffer result = new StringBuffer() ;
        final int length = uri.length() ;
        int separatorCount = 0 ;
        for(int count = 0 ; count < length ; count++)
        {
            final char ch = uri.charAt(count) ;
            if (separatorCount < 3)
            {
                if (ch == '/')
                {
                    separatorCount++ ;
                    result.append('-') ;
                }
                else if (ch == ':')
                {
                    result.append('_') ;
                }
                else if (ch == '[' || ch == ']')
                {
                    result.append('~') ;
                }
                else
                {
                    result.append(ch) ;
                }
            }
            else
            {
                result.append(ch) ;
            }
        }
        return result.toString() ;
    }
}