package com.raquo.laminar

import com.raquo.laminar.api.A._
import com.raquo.laminar.api.L.svg._
import com.raquo.laminar.api.L.{svg => s}
import com.raquo.laminar.api._
import com.raquo.laminar.utils.UnitSpec

class SvgSpec extends UnitSpec {

  it("renders sample svg, sets attrs and responds to events") {

    val strokeWidthVar = Var("3")

    var clickCount = 0

    val polylineEl = polyline(
      points := "20,20 40,25 60,40 80,120 120,140 200,180",
      fill := "none",
      stroke := "black",
      s.className := "classy",
      strokeWidth <-- strokeWidthVar.signal,
      L.onClick --> (_ => clickCount += 1)
    )

    val el = svg(
      height := "800",
      width := "500",
      polylineEl
    )

    mount(L.div(el))

    expectNode(L.div like (svg like(
      height is "800",
      width is "500",
      polyline like(
        points is "20,20 40,25 60,40 80,120 120,140 200,180",
        fill is "none",
        stroke is "black",
        s.className is "classy",
        strokeWidth is "3"
      )
    )))

    // --

    (stroke := "red").apply(polylineEl)

    expectNode(L.div like (svg like(
      height is "800",
      width is "500",
      polyline like(
        points is "20,20 40,25 60,40 80,120 120,140 200,180",
        fill is "none",
        stroke is "red", // <-- the change
        s.className is "classy",
        strokeWidth is "3"
      )
    )))

    // --

    strokeWidthVar.writer.onNext("4")

    expectNode(L.div like (svg like(
      height is "800",
      width is "500",
      polyline like(
        points is "20,20 40,25 60,40 80,120 120,140 200,180",
        fill is "none",
        stroke is "red", // <-- the change
        strokeWidth is "4",
        s.className is "classy"
      )
    )))

    polylineEl.maybeEventListeners.get.length shouldBe 1
    clickCount shouldBe 0

    // One event listener added
    simulateClick(polylineEl.ref)
    clickCount shouldBe 1

    unmount()
  }
}
