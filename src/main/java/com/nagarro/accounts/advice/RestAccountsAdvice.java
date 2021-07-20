package com.nagarro.accounts.advice;
 
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.nagarro.accounts.persistence.entity.Statement;
 
@RestControllerAdvice
public class RestAccountsAdvice implements ResponseBodyAdvice<List<Statement>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAccountsAdvice.class);
 
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
 
        String className = returnType.getContainingClass().toString();
        Method method = returnType.getMethod();
        String methodName = method == null ? "" : method.toString();
        
        return className.contains("AccountsController") && methodName.contains("getStatements");
    }
 
    @Override
    public List<Statement> beforeBodyWrite(List<Statement> statements, MethodParameter returnType,
            MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		for (Statement statement : statements) {
			String accountNumber = statement.getAccount().getNumber();

			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA3-256");
				final byte[] hashbytes = digest.digest(accountNumber.getBytes(StandardCharsets.UTF_8));
				String sha3Hex = bytesToHex(hashbytes);

				statement.getAccount().setNumber(sha3Hex);
			} catch (NoSuchAlgorithmException exception) {
				LOGGER.error("Error during hashing ", exception);
			}
		}

		return statements;
	}
    
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
 
}