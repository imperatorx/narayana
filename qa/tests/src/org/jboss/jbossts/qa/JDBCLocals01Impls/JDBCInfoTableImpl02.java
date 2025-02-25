/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



//

package org.jboss.jbossts.qa.JDBCLocals01Impls;

import java.sql.*;
import java.util.Properties;

public class JDBCInfoTableImpl02 implements InfoTable
{
	public JDBCInfoTableImpl02(String databaseURL, String databaseUser, String databasePassword, String databaseDynamicClass, int timeout)
			throws InvocationException
	{
		_databaseUser = databaseUser;
		try
		{
			if (databaseDynamicClass != null)
			{
				_databaseURL = databaseURL;

				_databaseProperties = new Properties();
				_databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.userName, databaseUser);
				_databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.password, databasePassword);
				_databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.dynamicClass, databaseDynamicClass);
			}
			else
			{
				_databaseURL = databaseURL;
				_databaseUser = databaseUser;
				_databasePassword = databasePassword;
				_databaseProperties = null;
			}
			_databaseTimeout = timeout;

			//create first connection to get metadata
			Connection connection;
			if (_databaseProperties != null)
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
			}
			else
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
			}

			DatabaseMetaData dbmd = connection.getMetaData();
			if (dbmd.getDatabaseProductName().startsWith("Microsoft"))
			{
				_useTimeout = true;
			}

			connection.close();

			_transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl02.JDBCInfoTableImpl02: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	public void insert(String name, String value)
			throws InvocationException
	{
		try
		{
			System.err.println("02------------------ doing insert (" + name + "," + value + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Connection connection;
			if (_databaseProperties != null)
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
			}
			else
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
			}

			Statement statement = connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("INSERT INTO " + _databaseUser + "_InfoTable VALUES(\'" + name + "\', \'" + value + "\')");
			statement.executeUpdate("INSERT INTO " + _databaseUser + "_InfoTable VALUES(\'" + name + "\', \'" + value + "\')");

			statement.close();
			connection.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl02.insert: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	public void update(String name, String value)
			throws InvocationException
	{
		try
		{
			System.err.println("02------------------ doing update (" + name + "," + value + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Connection connection;
			if (_databaseProperties != null)
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
			}
			else
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
			}

			Statement statement = connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("UPDATE " + _databaseUser + "_InfoTable SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");
			statement.executeUpdate("UPDATE " + _databaseUser + "_InfoTable SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");

			statement.close();
			connection.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl02.update: " + exception);
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
			System.err.println("02------------------ doing select (" + name + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Connection connection;
			if (_databaseProperties != null)
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
			}
			else
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
			}

			Statement statement = connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("SELECT Value FROM " + _databaseUser + "_InfoTable WHERE Name = \'" + name + "\'");
			ResultSet resultSet = statement.executeQuery("SELECT Value FROM " + _databaseUser + "_InfoTable WHERE Name = \'" + name + "\'");

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
			connection.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl02.select: " + exception);
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
			System.err.println("02------------------ doing delete (" + name + ") -----------------------------");
			System.err.println("Current Status = " + _transactionManager.getStatus());
			Connection connection;
			if (_databaseProperties != null)
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
			}
			else
			{
				connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
			}

			Statement statement = connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}

			System.err.println("DELETE FROM " + _databaseUser + "_InfoTable WHERE Name = \'" + name + "\'");
			statement.executeUpdate("DELETE FROM " + _databaseUser + "_InfoTable WHERE Name = \'" + name + "\'");

			statement.close();
			connection.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl02.delete: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	private String _databaseURL;
	private String _databaseUser;
	private String _databasePassword;
	private int _databaseTimeout;
	private Properties _databaseProperties;
	private boolean _useTimeout = false;
	private jakarta.transaction.TransactionManager _transactionManager;

}