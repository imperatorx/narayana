/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jbossatx.jts;

import org.omg.CosTransactions.*;

import java.io.*;

import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.jts.utils.Utility;

/**
 * This class is a wrapper around a PropagationContext object allowing it to be serialized.
 *
 * @author Richard A. Begg (richard.begg@arjuna.com)
 * @version $Id: PropagationContextWrapper.java,v 1.5 2004/10/04 09:48:19 nmcl Exp $
 */

public class PropagationContextWrapper implements Externalizable
{
	private static boolean _propagateFullContext = false;

	private boolean _isNull = true;
	private int _timeout;
	private TransIdentityWrapper _current;
	private TransIdentityWrapper[] _parents = null;

	private PropagationContext _tpc = null;

	public static void setPropagateFullContext(boolean propagateFullContext)
	{
		_propagateFullContext = propagateFullContext;
	}

	public static boolean getPropagateFullContext()
	{
		return _propagateFullContext;
	}

	/**
	 * Default constructor required for serialization
	 */

	public PropagationContextWrapper()
	{
	}

	/**
	 * Create a wrapper around a propagation context class
	 * @param tpc
	 */

	public PropagationContextWrapper(PropagationContext tpc)
	{
		this();

		_isNull = (tpc == null);

		if (tpc != null)
		{
			_current = new TransIdentityWrapper();

			_current._coordinator = ORBManager.getORB().orb().object_to_string(tpc.current.coord);
			_current.setOtid(tpc.current.otid);

			_timeout = tpc.timeout;

			if (_propagateFullContext)
			{
				_current._terminator = ORBManager.getORB().orb().object_to_string(tpc.current.term);

				_parents = new TransIdentityWrapper[tpc.parents.length];

				for (int count = 0; count < tpc.parents.length; count++)
				{
					_parents[count] = new TransIdentityWrapper();
					_parents[count]._coordinator = ORBManager.getORB().orb().object_to_string(tpc.parents[count].coord);
					_parents[count]._terminator = ORBManager.getORB().orb().object_to_string(tpc.parents[count].term);
					_parents[count].setOtid(tpc.parents[count].otid);
				}
			}
		}
	}

	public int hashCode()
	{
		return _isNull ? 0 : _current.hashCode();
	}

	public boolean equals(Object o)
	{
		if (o instanceof PropagationContextWrapper)
		{
			PropagationContextWrapper comp = (PropagationContextWrapper) o;

			if (!_isNull && !comp._isNull)
			{
				return (_current.equals(comp._current));
			}
		}

		return false;
	}

	// this is called on the remote side

	public PropagationContext getPropagationContext()
	{
		if (_isNull)
		{
			return null;
		}

		if (_tpc == null)
		{
			if (_propagateFullContext)
			{
				TransIdentity[] parents = new TransIdentity[_parents != null ? _parents.length : 0];

				for (int count = 0; count < parents.length; count++)
				{
					parents[count] = _parents[count].getTransIdentity();
				}

				_tpc = new PropagationContext(_timeout,
						_current.getTransIdentity(),
						parents,
						null);
			}
			else
			{
				_tpc = new PropagationContext(_timeout,
						_current.getTransIdentity(),
						new TransIdentity[0], // no parents, but not null
						null);
			}
		}

		return _tpc;
	}

	public void writeExternal(ObjectOutput out) throws IOException
	{
		try
		{
			out.writeBoolean(_propagateFullContext);
			out.writeBoolean(_isNull);

			if (!_isNull)
			{
				out.writeInt(_timeout);
				_current.writeExternal(out, _propagateFullContext);

				if (_propagateFullContext)
				{
					out.writeInt(_parents.length);

					for (int count = 0; count < _parents.length; count++)
					{
						_parents[count].writeExternal(out, _propagateFullContext);
					}
				}
			}
		}
		catch (Exception e)
		{
            IOException ioException = new IOException(e.toString());
            ioException.initCause(e);
            throw ioException;
        }
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		try
		{
			boolean fullContext = in.readBoolean();

			_isNull = in.readBoolean();

			if (!_isNull)
			{
				_timeout = in.readInt();
				_current = new TransIdentityWrapper();
				_current.readExternal(in, fullContext);

				if (fullContext)
				{
					_parents = new TransIdentityWrapper[in.readInt()];

					for (int count = 0; count < _parents.length; count++)
					{
						_parents[count] = new TransIdentityWrapper();
						_parents[count].readExternal(in, fullContext);
					}
				}
			}

			_tpc = null;
		}
		catch (Exception e)
		{
            IOException ioException = new IOException(e.toString());
            ioException.initCause(e);
            throw ioException;
		}
	}

	/**
	 * A wrapper around a transidentity object so that it can be serialized.
	 */

	private class TransIdentityWrapper implements Serializable
	{
		public String _coordinator = null;
		private otid_t _otid = null;
		public String _terminator = null;
		private int _hashCode = 0;

		public TransIdentity getTransIdentity()
		{
			return new TransIdentity(CoordinatorHelper.narrow(ORBManager.getORB().orb().string_to_object(_coordinator)),
					_terminator == null ? null : TerminatorHelper.narrow(ORBManager.getORB().orb().string_to_object(_terminator)),
					_otid);
		}

		public otid_t getOtid()
		{
			return _otid;
		}

		public void setOtid(otid_t o)
		{
			_otid = o;
			_hashCode = Utility.otidToUid(_otid).hashCode();
		}

		private boolean same(otid_t otid1, otid_t otid2)
		{
			if ((otid1.formatID == otid2.formatID) &&
					(otid1.bqual_length == otid2.bqual_length))
			{
				for (int i = 0; i < otid1.bqual_length; i++)
				{
					if (otid1.tid[i] != otid2.tid[i])
						return false;
				}

				/*
				 * Got here, so must be equal!
				 */

				return true;
			}
			else
				return false;
		}

		public int hashCode()
		{
			return _hashCode;
		}

		public boolean equals(Object o)
		{
			if (o instanceof TransIdentityWrapper)
			{
				TransIdentityWrapper t = (TransIdentityWrapper) o;

				return _otid != null && t._otid != null && same(_otid, t._otid);
			}

			return false;
		}

		/**
		 * The object implements the writeExternal method to save its contents
		 * by calling the methods of DataOutput for its primitive values or
		 * calling the writeObject method of ObjectOutput for objects, strings,
		 * and arrays.
		 *
		 * @serialData Overriding methods should use this tag to describe
		 *             the data layout of this Externalizable object.
		 *             List the sequence of element types and, if possible,
		 *             relate the element to a public/protected field and/or
		 *             method of this Externalizable class.
		 *
		 * @param out the stream to write the object to
		 * @exception IOException Includes any I/O exceptions that may occur
		 */

		public void writeExternal(ObjectOutput out, boolean fullContext) throws IOException
		{
			out.writeObject(_coordinator);
			out.writeObject(getOtid());

			if (fullContext)
			{
				out.writeObject(_terminator);
			}
		}

		public void readExternal(ObjectInput in, boolean fullContext) throws IOException, ClassNotFoundException
		{
			_coordinator = (String) in.readObject();
			setOtid((otid_t) in.readObject());

			if (fullContext)
			{
				_terminator = (String) in.readObject();
			}
			else
			{
				_terminator = null;
			}
		}
	}
}