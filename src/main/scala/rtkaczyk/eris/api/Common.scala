package rtkaczyk.eris.api

trait Common {
  //val TAG = this.getClass.getName
  val TAG = "eris." + this.getClass.getSimpleName
  val ErisServiceClass = "rtkaczyk.eris.service.ErisService"
  val ErisPermission = "rtkaczyk.eris.permission.ERIS"
  def now = System.currentTimeMillis
}