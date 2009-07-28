package de.macsystems.windroid.alarm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class AlarmDetail implements Parcelable
{
	private final int alarmID;

	private final String name;

	private final String stationID;

	public AlarmDetail(final int _alarmID, final String _stationID, final String _name)
	{
		alarmID = _alarmID;
		stationID = _stationID;
		name = _name;
	}

	private AlarmDetail(final Parcel in)
	{
		alarmID = in.readInt();
		name = in.readString();
		stationID = in.readString();
	}

	/**
	 * Returns the Alarm ID
	 * 
	 * @return
	 */
	public int getId()
	{
		return alarmID;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the station id
	 */
	public String getStationID()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(final Parcel out, final int flags)
	{
		out.writeInt(alarmID);
		out.writeString(name);
		out.writeString(stationID);
	}

	public static final Parcelable.Creator<AlarmDetail> CREATOR = new Parcelable.Creator<AlarmDetail>()
	{
		public AlarmDetail createFromParcel(final Parcel in)
		{
			return new AlarmDetail(in);
		}

		public AlarmDetail[] newArray(final int size)
		{
			return new AlarmDetail[size];
		}
	};
}