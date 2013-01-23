package rtkaczyk.eris.api

import android.content.Intent
import android.content.Context
import android.content.BroadcastReceiver
import android.content.IntentFilter
import java.text.SimpleDateFormat

object Events {
  
  val PREFIX = "rtkaczyk.eris.api.event."
  
  val EV_ERIS_STARTED        = PREFIX + "ERIS_STARTED"
  val EV_ERIS_STOPPED        = PREFIX + "ERIS_STOPPED"
  val EV_DISCOVERY_STARTED   = PREFIX + "DISCOVERY_STARTED"
  val EV_DISCOVERY_FINISHED  = PREFIX + "DISCOVERY_FINISHED"
  val EV_DISCOVERY_REFUSED   = PREFIX + "DISCOVERY_REFUSED"
  val EV_DEVICE_FOUND        = PREFIX + "DEVICE_FOUND"
  val EV_RECEIVING_FINISHED  = PREFIX + "RECEIVING_FINISHED"
  val EV_RECEIVING_FAILED    = PREFIX + "RECEIVING_FAILED"
  val EV_RECEIVING_REFUSED   = PREFIX + "RECEIVING_REFUSED"
  val EV_PACKETS_RECEIVED    = PREFIX + "PACKETS_RECEIVED"
  val EV_PACKETS_STORED      = PREFIX + "PACKETS_STORED"
  val EV_QUERY_COMPLETED     = PREFIX + "QUERY_COMPLETED"
  val EV_PACKETS_COUNT       = PREFIX + "PACKETS_COUNT"
  val EV_FORWARDING_STARTED  = PREFIX + "FORWARDING_STARTED"
  val EV_FORWARDING_REFUSED  = PREFIX + "FORWARDING_REFUSED"
  val EV_FORWARDING_FAILED   = PREFIX + "FORWARDING_FAILED"
  val EV_FORWARDING_FINISHED = PREFIX + "FORWARDING_FINISHED"
  val EV_PACKETS_FORWARDED   = PREFIX + "PACKETS_FORWARDED"

  val EventList: List[String] = for {
    m <- Events.getClass.getMethods.toList if m.getName startsWith "EV_"
  } yield m.invoke(Events).toString
  
  
  
  object ErisEvent {
    def apply(intent: Intent): ErisEvent = intent.getAction match {
      
      case EV_ERIS_STARTED => 
        ErisStarted
        
      case EV_ERIS_STOPPED => 
        ErisStopped
        
      case EV_DISCOVERY_STARTED =>
        DiscoveryStarted
        
      case EV_DISCOVERY_FINISHED =>
        DiscoveryFinished
        
      case EV_DISCOVERY_REFUSED =>
        DiscoveryRefused
        
      case EV_DEVICE_FOUND =>
        DeviceFound(DeviceId(intent getStringExtra "device"))
        
      case EV_RECEIVING_FINISHED =>
        ReceivingFinished(
            DeviceId(intent getStringExtra "device"),
            intent getIntExtra ("received", 0),
            intent getLongExtra ("from", 0),
            intent getLongExtra ("to", 0)
        )
        
      case EV_RECEIVING_FAILED =>
        ReceivingFailed(
            DeviceId(intent getStringExtra "device"),
            intent getStringExtra "cause"
        )
        
      case EV_RECEIVING_REFUSED =>
        ReceivingRefused(DeviceId(intent getStringExtra "device"))
        
      case EV_PACKETS_RECEIVED =>
        PacketsReceived(
            DeviceId(intent getStringExtra "device"),
            intent getIntExtra ("received", 0),
            intent getIntExtra ("all", 0)
        )
        
      case EV_PACKETS_STORED =>
        PacketsStored(
            DeviceId(intent getStringExtra "device"),
            intent getIntExtra ("stored", 0)
        )
        
      case EV_QUERY_COMPLETED =>
        QueryCompleted(intent getIntExtra ("id", -1))
        
      case EV_PACKETS_COUNT =>
        PacketsCount(intent getIntExtra ("n", 0))
        
      case EV_FORWARDING_STARTED =>
        ForwardingStarted(
            intent getStringExtra "addr",
            intent getIntExtra ("port", 0)
        )
        
      case EV_FORWARDING_REFUSED =>
        ForwardingRefused
        
      case EV_FORWARDING_FAILED =>
        ForwardingFailed(intent getStringExtra "cause")
        
      case EV_FORWARDING_FINISHED =>
        ForwardingFinished(
            intent getIntExtra ("confirmed", 0),
            intent getIntExtra ("all", 0)
        )
        
      case EV_PACKETS_FORWARDED =>
        PacketsForwarded(
            intent getIntExtra ("sent", 0),
            intent getIntExtra ("all", 0)
        )
        
      case _ =>
        UnknownEvent
    }
  }
  
  sealed trait ErisEvent {
    def toIntent: Intent = new Intent(event)
    def event: String
    override def toString = event + description.getOrElse("")
    def shortEvent = event stripPrefix PREFIX
    def description: Option[String] = None
  }
  
  
  case object UnknownEvent extends ErisEvent {
    val event = "# Uknown event #"
  }
  
  case object ErisStarted extends ErisEvent {
    val event = EV_ERIS_STARTED
  }
  
  case object ErisStopped extends ErisEvent {
    val event = EV_ERIS_STOPPED
  }
  
  case object DiscoveryStarted extends ErisEvent {
    val event = EV_DISCOVERY_STARTED
  }
  
  case object DiscoveryFinished extends ErisEvent {
    val event = EV_DISCOVERY_FINISHED
  }
  
  case object DiscoveryRefused extends ErisEvent {
    val event = EV_DISCOVERY_REFUSED
  }
  
  case class DeviceFound(device: DeviceId) extends ErisEvent {
    val event = EV_DEVICE_FOUND
    override def toIntent = new Intent(event) putExtra ("device", device.id)
    override def description = Some(" { device = [%s] }" format device)
  }
  
  case class ReceivingFinished(device: DeviceId, received: Int, from: Long, to: Long) 
  extends ErisEvent {
    val event = EV_RECEIVING_FINISHED
    override def toIntent = ( 
      new Intent(event) 
      putExtra ("device", device.id)
      putExtra ("received", received)
      putExtra ("from", from)
      putExtra ("to", from)
    )
    override def description = {
      if (received > 0) {
          val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS")
          Some(" { device: [%s], received = [%d], from = [%s], to = [%s] }" 
            format (device, received, sdf format from, sdf format to))
        } else {
        Some(" { device: [%s], received = [%d] }" 
          format (device, received))
      }
    }
  }
  
  case class ReceivingFailed(device: DeviceId, cause: String) extends ErisEvent {
    val event = EV_RECEIVING_FAILED
    override def toIntent = ( 
      new Intent(event) 
      putExtra ("device", device.id)
      putExtra ("cause", cause)
    )
    override def description = Some(" { device: [%s], cause = [%s] }" 
        format (device, cause))
  }
  
  case class ReceivingRefused(device: DeviceId) extends ErisEvent {
    val event = EV_RECEIVING_REFUSED
    override def toIntent = new Intent(event) putExtra ("device", device.id)
    override def description = Some(" { device = [%s] }" format device)
  }
    
  case class PacketsReceived(device: DeviceId, received: Int, all: Int) extends ErisEvent {
    val event = EV_PACKETS_RECEIVED
    override def toIntent = (
        new Intent(event) 
        putExtra ("device", device.id)
        putExtra ("received", received)
        putExtra ("all", all)
    )
    override def description = Some(" { device = [%s], received = [%d/%d] }" 
        format (device, received, all))
  }
  
  case class PacketsStored(device: DeviceId, stored: Int) extends ErisEvent {
    val event = EV_PACKETS_STORED
    override def toIntent = (
        new Intent(event)
        putExtra ("device", device.id)
        putExtra ("stored", stored)
    )
    override def description = Some(" { device = [%s], stored = [%d] }"
        format (device, stored))
  }
  
  case class QueryCompleted(id: Int) extends ErisEvent {
    val event = EV_QUERY_COMPLETED
    override def toIntent = (
        new Intent(event)
        putExtra ("id", id)
    )
    override def description = Some(" { id = [%d] }" format id)
  }
  
  case class PacketsCount(n: Int) extends ErisEvent {
    val event = EV_PACKETS_COUNT
    override def toIntent = (
        new Intent(event)
        putExtra("n", n)
    )
    override def description = Some(" { n = [%d] }" format n)
  }
  
  case class ForwardingStarted(addr: String, port: Int) extends ErisEvent {
    val event = EV_FORWARDING_STARTED
    override def toIntent = ( 
        new Intent(event)
        putExtra ("addr", addr)
        putExtra ("port", port)
    )
    override def description = Some(" { addr = [%s], port = [%d] }" format (addr, port))
  }
  
  case object ForwardingRefused extends ErisEvent {
    val event = EV_FORWARDING_REFUSED
  }
  
  case class ForwardingFailed(cause: String) extends ErisEvent {
    val event = EV_FORWARDING_FAILED
    override def toIntent = (
        new Intent(event)
        putExtra ("cause", cause)
    )
    override def description = Some(" { cause = [%s] }" format cause)
  }
  
  case class ForwardingFinished(confirmed: Int, all: Int) extends ErisEvent {
    val event = EV_FORWARDING_FINISHED
    override def toIntent = (
        new Intent(event)
        putExtra ("confirmed", confirmed)
        putExtra ("all", all)
    )
    override def description = Some(" { confirmed = [%d/%d] }" format (confirmed, all))
  }
  
  case class PacketsForwarded(sent: Int, all: Int) extends ErisEvent {
    val event = EV_PACKETS_FORWARDED
    override def toIntent = (
        new Intent(event)
        putExtra("sent", sent)
        putExtra("all", all)
    )
    override def description = Some(" { sent = [%d/%d] }" format (sent, all))
  }
  
  
  
  object EventReceiver {
    def apply(react: PartialFunction[ErisEvent, Unit]) = new EventReceiver(react)
  }
  
  class EventReceiver(val react: PartialFunction[ErisEvent, Unit]) {
    private val receiver = new BroadcastReceiver {
      override def onReceive(context: Context, intent: Intent) {
        val ev = ErisEvent(intent)
        if (react isDefinedAt ev)
          react(ev)
      }
    }
    
    private val filter = new IntentFilter
    EventList foreach filter.addAction
    
    def registerWith(context: Context) = context registerReceiver (receiver, filter)
    def unregisterWith(context: Context) = context unregisterReceiver receiver
  }
}
