package eu.palaio.pddlplanning.test

import eu.palaio.pddlplanning.PlanSearchFunction
import eu.palaio.pddlplanning.Task
import eu.palaio.pddlplanning.test.PlanningUnitTest.Companion.scratchDomain
import eu.palaio.pddlplanning.test.PlanningUnitTest.Companion.scratchProblem
import eu.palaio.pddlplanning.test.PlanningUnitTest.Companion.simpleServiceDomain
import eu.palaio.pddlplanning.test.PlanningUnitTest.Companion.simpleServiceProblem
import io.mockk.coEvery
import io.mockk.mockk

/**
 * This unit test checks the test helpers that do not require a context to work.
 * It is an example for testing third-party planner implementations.
 */
class ExamplePlanningUnitTest : PlanningUnitTest {
    private val searchPlanMock = mockk<PlanSearchFunction>().also {
        coEvery {
            it.invoke(scratchDomain, scratchProblem, any())
        } returns listOf(Task.create("show_menu", "user"))

        coEvery {
            it.invoke(simpleServiceDomain, simpleServiceProblem, any())
        } returns listOf()
    }
    override val searchPlan: PlanSearchFunction = searchPlanMock
}