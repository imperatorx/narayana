/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


//

package org.jboss.jbossts.qa.JDBCResources04Impls;

import org.jboss.jbossts.qa.JDBCResources04.*;
import org.jboss.jbossts.qa.Utils.OTS;
import org.omg.CORBA.IntHolder;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Status;

import java.sql.*;
import java.util.Properties;

public class JDBCNumberTableImpl02 implements NumberTableOperations
{
	public JDBCNumberTableImpl02(String databaseURL, String databaseUser, String databasePassword, String databaseDynamicClass, int timeout)
			throws InvocationException
	{
		_dbUser = databaseUser;
		_databaseURL = databaseURL;
		_dbUser = databaseUser;
		_databasePassword = databasePassword;
		_databaseDynamicClass = databaseDynamicClass;
		_databaseTimeout = timeout;

		try
		{
			Connection connection = getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			if (dbmd.getDatabaseProductName().startsWith("Microsoft"))
			{
				System.err.println("SQLServer message");
				_useTimeout = true;
			}
			else if (dbmd.getDatabaseProductName().equals("DBMS:cloudscape"))
			{
				System.err.println("setting CLOUD message");
			}

			connection.close();
		}
		catch (Exception e)
		{
			System.err.println("JDBCNumberTableImpl01.JDBCNumberTableImpl01: " + e);
			throw new InvocationException();
		}
	}

	public void get(String name, IntHolder value, Control ctrl)
			throws InvocationException
	{
		Statement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try
		{
			com.arjuna.ats.jts.ExplicitInterposition interposition = new com.arjuna.ats.jts.ExplicitInterposition();
			interposition.registerTransaction(ctrl);

			try
			{
				System.err.println("-- get called --");
				connection = getConnection();
				statement = connection.createStatement();

				if (_useTimeout)
				{
					statement.setQueryTimeout(_databaseTimeout);
				}

				System.err.println("SELECT Value FROM " + _dbUser + "_NumberTable WHERE Name = \'" + name + "\'");
				resultSet = statement.executeQuery("SELECT Value FROM " + _dbUser + "_NumberTable WHERE Name = \'" + name + "\'");
				resultSet.next();
				value.value = resultSet.getInt("Value");
				if (resultSet.next())
				{
					throw new Exception();
				}
			}
			catch (Exception exception)
			{
				System.err.println("JDBCNumberTableImpl02.get: " + exception);
				throw new InvocationException(Reason.ReasonUnknown);
			}
			catch (Error error)
			{
				System.err.println("JDBCNumberTableImpl02.get: " + error);
				throw new InvocationException(Reason.ReasonUnknown);
			}
			finally
			{
				if ("true".equals(System.getProperty("qa.debug")))
				{
					System.err.println("Performing explicit commit for non-transaction operation");
				}
				if (OTS.current().get_status().value() == Status._StatusNoTransaction)
				{
					try
					{
						connection.commit();
					}
					catch (Exception e)
					{
						System.err.println("Ignoring exception: " + e);
						e.printStackTrace(System.err);
					}
				}
				if ("true".equals(System.getProperty("qa.debug")))
				{
					System.err.println("Closing connection");
				}
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
				try
				{
					interposition.unregisterTransaction();
				}
				catch (Exception e)
				{
					System.err.println("Ignoring exception: " + e);
					e.printStackTrace(System.err);
				}
				try
				{
					if (connection != null)
					{
						connection.close();
					}
				}
				catch (Exception e)
				{
					System.err.println("Ignoring exception: " + e);
					e.printStackTrace(System.err);
				}
			}
		}
		catch (InvocationException invocationException)
		{
			throw invocationException;
		}
		catch (Exception exception)
		{
			System.err.println("JDBCNumberTableImpl02.get: " + exception);
			throw new InvocationException(Reason.ReasonUnknown);
		}
	}

	public void set(String name, int value, Control ctrl)
			throws InvocationException
	{
		Statement statement = null;
		Connection connection = null;

		try
		{
			com.arjuna.ats.jts.ExplicitInterposition interposition = new com.arjuna.ats.jts.ExplicitInterposition();
			interposition.registerTransaction(ctrl);

			try
			{
				System.err.println("-- get called --");
				connection = getConnection();
				statement = connection.createStatement();
				if (_useTimeout)
				{
					statement.setQueryTimeout(_databaseTimeout);
				}

				System.err.println("UPDATE " + _dbUser + "_NumberTable SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");
				statement.executeUpdate("UPDATE " + _dbUser + "_NumberTable SET Value = \'" + value + "\' WHERE Name = \'" + name + "\'");
			}
			catch (Exception exception)
			{
				System.err.println("JDBCNumberTableImpl02.set: " + exception);
				throw new InvocationException(Reason.ReasonUnknown);
			}
			catch (Error error)
			{
				System.err.println("JDBCNumberTableImpl02.set: " + error);
				throw new InvocationException(Reason.ReasonUnknown);
			}
			finally
			{
				if ("true".equals(System.getProperty("qa.debug")))
				{
					System.err.println("Performing explicit commit for non-transaction operation");
				}
				if (OTS.current().get_status().value() == Status._StatusNoTransaction)
				{
					try
					{
						connection.commit();
					}
					catch (Exception e)
					{
						System.err.println("Ignoring exception: " + e);
						e.printStackTrace(System.err);
					}
				}
				if ("true".equals(System.getProperty("qa.debug")))
				{
					System.err.println("Closing connection");
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
				try
				{
					interposition.unregisterTransaction();
				}
				catch (Exception e)
				{
					System.err.println("Ignoring exception: " + e);
					e.printStackTrace(System.err);
				}
				try
				{
					if (connection != null)
					{
						connection.close();
					}
				}
				catch (Exception e)
				{
					System.err.println("Ignoring exception: " + e);
					e.printStackTrace(System.err);
				}
			}
		}
		catch (InvocationException invocationException)
		{
			throw invocationException;
		}
		catch (Exception exception)
		{
			System.err.println("JDBCNumberTableImpl02.set: " + exception);
			throw new InvocationException(Reason.ReasonUnknown);
		}
	}

	public void increase(String name, Control ctrl)
			throws InvocationException
	{
		Statement statement = null;
		Connection connection = null;

		try
		{
			com.arjuna.ats.jts.ExplicitInterposition interposition = new com.arjuna.ats.jts.ExplicitInterposition();
			interposition.registerTransaction(ctrl);

			try
			{
				System.err.println("-- increase --");
				connection = getConnection();

				statement = connection.createStatement();
				if (_useTimeout)
				{
					statement.setQueryTimeout(_databaseTimeout);
				}

				System.err.println("UPDATE " + _dbUser + "_NumberTable SET Value = Value + 1 WHERE NAME = \'" + name + "\'");
				statement.executeUpdate("UPDATE " + _dbUser + "_NumberTable SET Value = Value + 1 WHERE NAME = \'" + name + "\'");
			}
			catch (Exception exception)
			{
				System.err.println("JDBCNumberTableImpl02.increase: " + exception);
				throw new InvocationException(Reason.ReasonUnknown);
			}
			catch (Error error)
			{
				System.err.println("JDBCNumberTableImpl02.increase: " + error);
				throw new InvocationException(Reason.ReasonUnknown);
			}
			finally
			{
				if ("true".equals(System.getProperty("qa.debug")))
				{
					System.err.println("Performing explicit commit for non-transaction operation");
				}
				if (OTS.current().get_status().value() == Status._StatusNoTransaction)
				{
					try
					{
						connection.commit();
					}
					catch (Exception e)
					{
						System.err.println("Ignoring exception: " + e);
						e.printStackTrace(System.err);
					}
				}
				if ("true".equals(System.getProperty("qa.debug")))
				{
					System.err.println("Closing connection");
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
				try
				{
					interposition.unregisterTransaction();
				}
				catch (Exception e)
				{
					System.err.println("Ignoring exception: " + e);
					e.printStackTrace(System.err);
				}
				try
				{
					if (connection != null)
					{
						connection.close();
					}
				}
				catch (Exception e)
				{
					System.err.println("Ignoring exception: " + e);
					e.printStackTrace(System.err);
				}
			}
		}
		catch (InvocationException invocationException)
		{
			throw invocationException;
		}
		catch (Exception exception)
		{
			System.err.println("JDBCNumberTableImpl02.increase: " + exception);
			throw new InvocationException(Reason.ReasonUnknown);
		}
	}

	private Connection getConnection()
			throws Exception
	{
		Connection connection = null;

		if ("true".equals(System.getProperty("qa.debug")))
		{
			System.err.println("Setting up connection");
		}
		try
		{
			if (_databaseDynamicClass != null)
			{
				Properties databaseProperties = new Properties();

				databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.userName, _dbUser);
				databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.password, _databasePassword);
				databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.dynamicClass, _databaseDynamicClass);

				connection = DriverManager.getConnection(_databaseURL, databaseProperties);
			}
			else
			{
				connection = DriverManager.getConnection(_databaseURL, _dbUser, _databasePassword);
			}

			if ("true".equals(System.getProperty("qa.debug")))
			{
				System.err.println("connection = " + connection);
				System.err.println("Database URL = " + _databaseURL);
			}
		}
		catch (Exception exception)
		{
			System.err.println("JDBCNumberTableImpl01.getConnection: " + exception);
			throw new Exception("error in getConnection:" + exception);
		}
		return connection;
	}

	private String _databaseURL;
	private String _dbUser;
	private String _databasePassword;
	private String _databaseDynamicClass;
	private int _databaseTimeout;
	private boolean _useTimeout = false;
}