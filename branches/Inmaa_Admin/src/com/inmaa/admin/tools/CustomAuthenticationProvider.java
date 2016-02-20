
package com.inmaa.admin.tools;

import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class CustomAuthenticationProvider extends JdbcDaoImpl {
   public CustomAuthenticationProvider(){
       super();
   }
}