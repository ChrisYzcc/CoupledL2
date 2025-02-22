package coupledL2.tl2chi

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config.Parameters
import coupledL2._
import coupledL2.prefetch.PrefetchReq

class LLCPrefetchTranlator()(implicit p: Parameters) extends TL2CHIL2Module with HasCHIOpcodes {
  val io = IO(new Bundle(){
    val in_pft_req = Flipped(Decoupled(new PrefetchReq))
    val out_chi_req = Decoupled(new CHIREQ())
  })

  val pft_req = io.in_pft_req
  val chi_req = io.out_chi_req

  val fullAddr = Cat(pft_req.bits.tag, pft_req.bits.set, 0.U(offsetBits.W))
  val pft_task = WireInit(0.U.asTypeOf(new TaskBundle()))
  pft_task.tag  := parseAddress(fullAddr)._1
  pft_task.set  := parseAddress(fullAddr)._2
  pft_task.off  := 0.U
  pft_task.chiOpcode.get  := PrefetchTgt
  pft_task.allowRetry.get := false.B

  chi_req.valid := pft_req.valid
  chi_req.bits  := pft_task.toCHIREQBundle()

  pft_req.ready := chi_req.ready
}