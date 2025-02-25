/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


//

package org.jboss.jbossts.qa.CrashRecovery11Impls;



/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */




import com.arjuna.ats.arjuna.coordinator.AddOutcome;
import com.arjuna.ats.arjuna.coordinator.BasicAction;
import org.jboss.jbossts.qa.CrashRecovery11.*;
import org.jboss.jbossts.qa.Utils.JDBCProfileStore;
import org.omg.CORBA.IntHolder;

import java.sql.*;
import java.util.Properties;

public class JDBCServiceImpl01 implements BeforeCrashServiceOperations
{
	public JDBCServiceImpl01(String rowName, String databaseURL, String databaseUser, String databasePassword, String databaseDynamicClass)
			throws InvocationException
	{
		_dbUser = databaseUser;
		try
		{
			_rowName = rowName;

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

			Statement statement = _connection.createStatement();

            String tableName = JDBCProfileStore.getTableName(databaseUser, "Service");

			statement.executeUpdate("INSERT INTO " + tableName + " VALUES ('" + _rowName + "', 0)");

			statement.close();
		}
		catch (Exception exception)
		{
			System.err.println("JDBCServiceImpl01.JDBCServiceImpl01: " + exception);
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
			System.err.println("JDBCServiceImpl01.finalize: " + exception);
			throw exception;
		}
	}

	public void set(int value)
			throws InvocationException
	{
		try
		{
			try
			{
				Statement statement = _connection.createStatement();

                String tableName = JDBCProfileStore.getTableName(_dbUser, "Service");

                statement.executeUpdate("UPDATE " + tableName + " SET VALUE="+value+" WHERE Name='"+_rowName+"'");

				statement.close();
			}
			catch (SQLException sqlException)
			{
				System.err.println("JDBCServiceImpl01.set: " + sqlException);

				throw new InvocationException();
			}
		}
		catch (InvocationException invocationException)
		{
			_isCorrect = false;
			throw invocationException;
		}
		catch (Exception exception)
		{
			_isCorrect = false;
			System.err.println("JDBCServiceImpl01.set: " + exception);
			throw new InvocationException();
		}
	}

	public void get(IntHolder value)
			throws InvocationException
	{
		try
		{
			try
			{
				Statement statement = _connection.createStatement();

                String tableName = JDBCProfileStore.getTableName(_dbUser, "Service");

				ResultSet resultSet = statement.executeQuery("SELECT Value FROM " + tableName +" WHERE Name = '" + _rowName + "'");
				resultSet.next();
				value.value = resultSet.getInt("Value");
				if (resultSet.next())
				{
					throw new Exception();
				}

				resultSet.close();
				statement.close();
			}
			catch (SQLException sqlException)
			{
				System.err.println("JDBCServiceImpl01.get: " + sqlException);

				throw new InvocationException();
			}
		}
		catch (InvocationException invocationException)
		{
			_isCorrect = false;
			throw invocationException;
		}
		catch (Exception exception)
		{
			_isCorrect = false;
			System.err.println("JDBCServiceImpl01.select: " + exception);
			throw new InvocationException();
		}
	}


	public void setStartCrashAbstractRecordAction(CrashBehavior action)
			throws InvocationException
	{
		try
		{
			try
			{
				if (action == CrashBehavior.CrashBehaviorCrashInCommit)
				{
					_isCorrect = _isCorrect && (BasicAction.Current().add(new StartCrashAbstractRecordImpl(StartCrashAbstractRecordImpl.CRASH_IN_COMMIT)) == AddOutcome.AR_ADDED);
				}
				else if (action == CrashBehavior.CrashBehaviorCrashInPrepare)
				{
					_isCorrect = _isCorrect && (BasicAction.Current().add(new StartCrashAbstractRecordImpl(StartCrashAbstractRecordImpl.CRASH_IN_PREPARE)) == AddOutcome.AR_ADDED);
				}
			}
			catch (Exception exception)
			{
				System.err.println("JDBCServiceImpl01.setStartCrashAbstractRecordAction: " + exception);

				throw new InvocationException();
			}
		}
		catch (InvocationException invocationException)
		{
			_isCorrect = false;
			throw invocationException;
		}
		catch (Exception exception)
		{
			_isCorrect = false;
			System.err.println("JDBCServiceImpl01.setStartCrashAbstractRecordAction: " + exception);
			throw new InvocationException();
		}
	}

	public void setEndCrashAbstractRecordAction(CrashBehavior action)
			throws InvocationException
	{
		try
		{
			try
			{
				if (action == CrashBehavior.CrashBehaviorCrashInCommit)
				{
					_isCorrect = _isCorrect && (BasicAction.Current().add(new EndCrashAbstractRecordImpl(EndCrashAbstractRecordImpl.CRASH_IN_COMMIT)) == AddOutcome.AR_ADDED);
				}
				else if (action == CrashBehavior.CrashBehaviorCrashInPrepare)
				{
					_isCorrect = _isCorrect && (BasicAction.Current().add(new EndCrashAbstractRecordImpl(EndCrashAbstractRecordImpl.CRASH_IN_PREPARE)) == AddOutcome.AR_ADDED);
				}
			}
			catch (Exception exception)
			{
				System.err.println("JDBCServiceImpl01.setEndCrashAbstractRecordAction: " + exception);

				throw new InvocationException();
			}
		}
		catch (InvocationException invocationException)
		{
			_isCorrect = false;
			throw invocationException;
		}
		catch (Exception exception)
		{
			_isCorrect = false;
			System.err.println("JDBCServiceImpl01.setEndCrashAbstractRecordAction: " + exception);
			throw new InvocationException();
		}
	}

	public boolean is_correct()
	{
		return _isCorrect;
	}

	private String _rowName;
	private Connection _connection;
	private boolean _isCorrect = true;
	private String _dbUser;
}