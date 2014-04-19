/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
 ...
**********************************************************************/
package org.datanucleus.identity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.jdo.JDONullIdentityException;

/**
 * This class is the abstract base class for all single field identity classes. A common case of application
 * identity uses exactly one persistent field in the class to represent identity. In this case, the
 * application can use a standard JDO class instead of creating a new user-defined class for the purpose.
 */
public abstract class SingleFieldId implements Externalizable, Comparable
{
    /** The class of the target object. */
    transient private Class targetClass;

    /** The name of the class of the target object. */
    private String targetClassName;

    /** The hashCode. */
    protected int hashCode;

    /** The key as an Object. */
    protected Object keyAsObject;

    /**
     * Constructor with target class.
     * @param pcClass the class of the target
     */
    protected SingleFieldId(Class pcClass)
    {
        if (pcClass == null)
            throw new NullPointerException();
        targetClass = pcClass;
        targetClassName = pcClass.getName();
    }

    /**
     * Constructor only for Externalizable.
     */
    public SingleFieldId()
    {
    }

    /**
     * Set the given key as the key for this instance. Compute the hash code for the instance.
     * @param key The key
     */
    protected void setKeyAsObject(Object key)
    {
        assertKeyNotNull(key);
        keyAsObject = key;
    }

    /**
     * Assert that the key is not null. Throw a JDONullIdentityException if the given key is null.
     * @param key The key
     */
    protected void assertKeyNotNull(Object key)
    {
        if (key == null)
        {
            // TODO Remove this JDO class
            throw new JDONullIdentityException("Cannot have an identity with null key");
        }
    }

    /**
     * Return the target class.
     * @return the target class.
     */
    public Class getTargetClass()
    {
        return targetClass;
    }

    /**
     * Return the target class name.
     * @return the target class name.
     */
    public String getTargetClassName()
    {
        return targetClassName;
    }

    /**
     * Return the key as an Object. The method is synchronized to avoid race conditions in multi-threaded
     * environments.
     * @return the key as an Object.
     * @since 2.0
     */
    public synchronized Object getKeyAsObject()
    {
        if (keyAsObject == null)
        {
            keyAsObject = createKeyAsObject();
        }
        return keyAsObject;
    }

    /**
     * Create the key as an Object.
     * @return the key as an Object;
     */
    protected abstract Object createKeyAsObject();

    /**
     * Check the class and class name and object type. If restored from serialization, class will be null so
     * compare class name.
     * @param obj the other object
     * @return true if the class or class name is the same
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj == null || this.getClass() != obj.getClass())
        {
            return false;
        }
        else
        {
            SingleFieldId other = (SingleFieldId) obj;
            if (targetClass != null && targetClass == other.targetClass)
                return true;
            return targetClassName.equals(other.targetClassName);
        }
    }

    /**
     * Return the hash code of the class name.
     * @return the hash code of the class name
     * @since 2.0
     */
    protected int hashClassName()
    {
        return targetClassName.hashCode();
    }

    /**
     * Return the cached hash code.
     * @return the cached hash code.
     */
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * Write to the output stream.
     * @param out the stream
     */
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(targetClassName);
        out.writeInt(hashCode);
    }

    /**
     * Read from the input stream. Creates a new instance with the target class name set
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        targetClass = null;
        targetClassName = (String) in.readObject();
        hashCode = in.readInt();
    }

    /**
     * Determine the ordering of identity objects. Only the class name is compared. This method is only used
     * by subclasses.
     * @param o Other identity
     * @return The relative ordering between the objects
     */
    protected int compare(SingleFieldId o)
    {
        return targetClassName.compareTo(o.targetClassName);
    }
}