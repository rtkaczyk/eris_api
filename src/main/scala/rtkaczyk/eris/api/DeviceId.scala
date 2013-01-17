package rtkaczyk.eris.api

import android.bluetooth.BluetoothDevice

object DeviceId {
  val Separator = '/'
  
  def apply(device: BluetoothDevice) = new DeviceId(device.getName, device.getAddress.toUpperCase)
  def apply(packet: Packet) = new DeviceId(packet.name, packet.address.toUpperCase)
  def apply(name: String, address: String) = new DeviceId(name, address.toUpperCase)
  def apply(id: String) = {
    val i = id lastIndexOf '/'
    val (name, address) = id splitAt (if (i >= 0) i else id.length)  
    new DeviceId(name, address.toUpperCase drop 1)
  }
  
  def unapply(device: DeviceId): Option[(String, String)] = {
    if (device.isValid)
      Some(device.name, device.address)
    else
      None
  }
  
  implicit def bluetoothDevice2deviceId(device: BluetoothDevice) = DeviceId(device)
  implicit def packet2deviceId(packet: Packet) = DeviceId(packet)
  implicit def string2deviceId(s: String) = DeviceId(s)
}

class DeviceId private (val name: String, val address: String) {
  
  def id = name + DeviceId.Separator + address
  def isValid = address matches """([\dA-F]{2}:){5}[\dA-F]{2}"""
  
  override def equals(o: Any) = o match {
    case o: DeviceId => id == o.id
    case s: String => id == s
    case _ => false
  }
  
  def canEqual(o: Any) = o match {
    case o: DeviceId => true
    case o: String => true
    case _ => false
  }
  
  override def hashCode = id.hashCode
  
  override def toString = id
}