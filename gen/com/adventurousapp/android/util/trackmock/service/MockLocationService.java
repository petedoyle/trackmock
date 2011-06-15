/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\workspace\\trackmock\\src\\com\\adventurousapp\\android\\util\\trackmock\\service\\MockLocationService.aidl
 */
package com.adventurousapp.android.util.trackmock.service;
/**
 * {@hide}
 */
public interface MockLocationService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.adventurousapp.android.util.trackmock.service.MockLocationService
{
private static final java.lang.String DESCRIPTOR = "com.adventurousapp.android.util.trackmock.service.MockLocationService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.adventurousapp.android.util.trackmock.service.MockLocationService interface,
 * generating a proxy if needed.
 */
public static com.adventurousapp.android.util.trackmock.service.MockLocationService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.adventurousapp.android.util.trackmock.service.MockLocationService))) {
return ((com.adventurousapp.android.util.trackmock.service.MockLocationService)iin);
}
return new com.adventurousapp.android.util.trackmock.service.MockLocationService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_isActive:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isActive();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_startPlayback:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.startPlayback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopPlayback:
{
data.enforceInterface(DESCRIPTOR);
this.stopPlayback();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.adventurousapp.android.util.trackmock.service.MockLocationService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public boolean isActive() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isActive, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void startPlayback(java.lang.String trackName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trackName);
mRemote.transact(Stub.TRANSACTION_startPlayback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopPlayback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopPlayback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_isActive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startPlayback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopPlayback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public boolean isActive() throws android.os.RemoteException;
public void startPlayback(java.lang.String trackName) throws android.os.RemoteException;
public void stopPlayback() throws android.os.RemoteException;
}
