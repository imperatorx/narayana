/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


//

package org.jboss.jbossts.qa.JDBCResources01Impls;

import org.jboss.jbossts.qa.JDBCResources01.*;
import org.jboss.jbossts.qa.Utils.OTS;
import org.jboss.jbossts.qa.Utils.JDBCProfileStore;
import org.omg.CORBA.StringHolder;

import java.sql.*;
import java.util.Properties;

public class JDBCInfoTableImpl01 implements InfoTableOperations
{
	public JDBCInfoTableImpl01(String databaseURL, String databaseUser, String databasePassword, String databaseDynamicClass, int timeout)
			throws InvocationException
	{
//set up variable for use in sql statements
		_dbUser = databaseUser;
		_databaseTimeout = timeout;

		if ("true".equals(System.getProperty("qa.debug")))
		{
			System.err.println("Setting up connection");
		}
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

			if ("true".equals(System.getProperty("qa.debug")))
			{
				System.err.println("connection = " + _connection);
				System.err.println("Database URL = " + databaseURL);
			}

			Runtime.getRuntime().addShutdownHook(new JDBC01ShutdownThread());
			DatabaseMetaData dbmd = _connection.getMetaData();
			if (dbmd.getDatabaseProductName().startsWith("Microsoft"))
			{
				_useTimeout = true;
			}
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.JDBCInfoTableImpl01: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
	}

	public void insert(String name, String value)
			throws InvocationException
	{
		Statement statement = null;

		try
		{
			System.err.println("01------------------ doing insert (" + name + "," + value + ") -----------------------------");
			System.err.println("Current Status = " + OTS.current().get_status().value());
			statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}
            
            String tableName = JDBCProfileStore.getTableName(_dbUser, "Infotable");
            
			System.err.println("INSERT INTO " + tableName + " VALUES(\'" + name + "\', \'" + value + "\')");
			statement.executeUpdate("INSERT INTO " + tableName + " VALUES(\'" + name + "\', \'" + value + "\')");
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.insert: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
		finally
		{
			try
			{
				if (statement != null)
				{
					statement.close();
				}
			}
			catch (Exception e)
			{
				System.err.println("Ignoring exception: " + e);
				e.printStackTrace(System.err);
			}
		}
	}

	public void update(String name, String value)
			throws InvocationException
	{
		Statement statement = null;

		try
		{
			System.err.println("01------------------ doing update (" + name + "," + value + ") -----------------------------");
			System.err.println("Current Status = " + OTS.current().get_status().value());
			statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}
            
            String tableName = JDBCProfileStore.getTableName(_dbUser, "Infotable");
            
			System.err.println("UPDATE " + tableName + " SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");
			statement.executeUpdate("UPDATE " + tableName + " SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.update: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
		finally
		{
			try
			{
				if (statement != null)
				{
					statement.close();
				}
			}
			catch (Exception e)
			{
				System.err.println("Ignoring exception: " + e);
				e.printStackTrace(System.err);
			}
		}
	}

	public void select(String name, StringHolder value)
			throws InvocationException
	{
		Statement statement = null;
		ResultSet resultSet = null;

		try
		{
			System.err.println("01------------------ doing select (" + name + ") -----------------------------");
			System.err.println("Current Status = " + OTS.current().get_status().value());
			statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}
            
            String tableName = JDBCProfileStore.getTableName(_dbUser, "Infotable");
            
			System.err.println("SELECT Value FROM " + tableName + " WHERE Name = \'" + name + "\'");
			resultSet = statement.executeQuery("SELECT Value FROM " + tableName + " WHERE Name = \'" + name + "\'");
			if (!resultSet.next())
			{
				throw new Exception("Result set is empty - expected a row");
			}
			value.value = resultSet.getString("Value");
			if (resultSet.next())
			{
				throw new Exception("Result set is not empty - didn't expect a row");
			}
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.select: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
		finally
		{
			try
			{
				if (resultSet != null)
				{
					resultSet.close();
				}
			}
			catch (Exception e)
			{
				System.err.println("Ignoring exception: " + e);
				e.printStackTrace(System.err);
			}
			try
			{
				if (statement != null)
				{
					statement.close();
				}
			}
			catch (Exception e)
			{
				System.err.println("Ignoring exception: " + e);
				e.printStackTrace(System.err);
			}
		}
	}

	public void delete(String name)
			throws InvocationException
	{
		Statement statement = null;

		try
		{
			System.err.println("01------------------ doing delete (" + name + ") -----------------------------");
			System.err.println("Current Status = " + OTS.current().get_status().value());
			statement = _connection.createStatement();
			if (_useTimeout)
			{
				statement.setQueryTimeout(_databaseTimeout);
			}
            
            String tableName = JDBCProfileStore.getTableName(_dbUser, "Infotable");
            
			System.err.println("DELETE FROM " + tableName + " WHERE Name = \'" + name + "\'");
			statement.executeUpdate("DELETE FROM " + tableName + " WHERE Name = \'" + name + "\'");
		}
		catch (Exception exception)
		{
			System.err.println("JDBCInfoTableImpl01.delete: " + exception);
			exception.printStackTrace(System.err);
			throw new InvocationException();
		}
		finally
		{
			try
			{
				if (statement != null)
				{
					statement.close();
				}
			}
			catch (Exception e)
			{
				System.err.println("Ignoring exception: " + e);
				e.printStackTrace(System.err);
			}
		}
	}

	private Connection _connection = null;
	private String _dbUser;
	private int _databaseTimeout;
	private boolean _useTimeout = false;

	/*
		 * We can't guarantee that finalize() will be called,
		 * so we have a thread that will close the database connection.
		 */
	private class JDBC01ShutdownThread extends Thread
	{
		public void run()
		{
			System.err.println("JDBCInfoTableImpl01.JDBC01ShutdownThread: running");
			try
			{
				if (_connection != null)
				{
					_connection.close();
				}
			}
			catch (Exception exception)
			{
				System.err.println("JDBCInfoTableImpl01.JDBC01ShutdownThread: " + exception);
				exception.printStackTrace(System.err);
			}
		}
	}
}