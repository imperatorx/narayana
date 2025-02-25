/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



//

package org.jboss.jbossts.qa.JDBCLocals01Impls;

import java.sql.*;
import java.util.Properties;

public class JDBCInfoTableImpl01 implements InfoTable
{
	public JDBCInfoTableImpl01(String databaseURL, String databaseUser, String databasePassword, String databaseDynamicClass, int timeout)
			throws InvocationException
	{
//set up variable for use in sql statements
		_dbUser = databaseUser;
		_databaseTimeout = timeout;
		try
		{
			if (databaseDynamicClass != null)
			{
				Properties databaseProperties = new Properties();

				databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.userName, databaseUser);
				databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.password, databasePassword);
				databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.dynamicClass, databaseDynamicClass);

				_connection = DriverManager.getConnection(databaseURL, databaseProperties);
			}
			else
			{
				_connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			}

			DatabaseMetaData dbmd = _connection.getMetaData();
			if (dbmd.getDatabaseProductName().startsWith("Microsoft"))
			{
				_useTimeout = true;
			}

			_transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.JDBCInfoTableImpl01: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	public void finalize()
			throws Throwable
	{
		try
		{
			if (_connection != null)
			{
				_connection.close();
			}
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.finalize: " + exception);
			exception.printStackTrace(System.err);
			throw exception;
		}
	}

	public void insert(String name, String value)
			throws InvocationException
	{
		try
		{
			System.err.println("01------------------ doing insert (" + name + "," + value + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Statement statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("INSERT INTO " + _dbUser + "_InfoTable VALUES(\'" + name + "\', \'" + value + "\')");
			statement.executeUpdate("INSERT INTO " + _dbUser + "_InfoTable VALUES(\'" + name + "\', \'" + value + "\')");

			statement.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.insert: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	public void update(String name, String value)
			throws InvocationException
	{
		try
		{
			System.err.println("01------------------ doing update (" + name + "," + value + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Statement statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("UPDATE " + _dbUser + "_InfoTable SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");
			statement.executeUpdate("UPDATE " + _dbUser + "_InfoTable SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");

			statement.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.update: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	public String select(String name)
			throws InvocationException
	{
		String value = "";

		try
		{
			System.err.println("01------------------ doing select (" + name + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Statement statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("SELECT Value FROM " + _dbUser + "_InfoTable WHERE Name = \'" + name + "\'");
			ResultSet resultSet = statement.executeQuery("SELECT Value FROM " + _dbUser + "_InfoTable WHERE Name = \'" + name + "\'");
			if (!resultSet.next())
			{
				throw new Exception("Result set is empty - expected a row");
			}
			value = resultSet.getString("Value");
			if (resultSet.next())
			{
				throw new Exception("Result set is not empty - didn't expect a row");
			}

			resultSet.close();
			statement.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.select: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}

		return value;
	}

	public void delete(String name)
			throws InvocationException
	{
		try
		{
			System.err.println("01------------------ doing delete (" + name + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Statement statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("DELETE FROM " + _dbUser + "_InfoTable WHERE Name = \'" + name + "\'");
			statement.executeUpdate("DELETE FROM " + _dbUser + "_InfoTable WHERE Name = \'" + name + "\'");

			statement.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.delete: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	private Connection _connection;
	private String _dbUser;
	private int _databaseTimeout;
	private boolean _useTimeout = false;
	private jakarta.transaction.TransactionManager _transactionManager;

}