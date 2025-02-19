package eu.palaio.pddlplanning.test

import android.content.Context
import eu.palaio.pddlplanning.*
import eu.palaio.pddlplanning.utils.searchPlanForInit
import kotlinx.coroutines.runBlocking
import org.junit.Assert


/**
 * Checks whether the right plan is found for a given init in a problem.
 * Note that it replaces the init of the problem.
 */
suspend fun checkPlanForInit(
    domain: String,
    problem: String,
    init: Collection<Expression>,
    searchPlan: PlanSearchFunction,
    logFunction: LogFunction,
    expectedPlan: Tasks
) {
    val plan = searchPlanForInit(domain, problem, init, searchPlan, logFunction)
    Assert.assertEquals(expectedPlan, plan)
}

/**
 * Checks whether the right plans are found for given inits in a problem.
 * Note that it replaces the init of the problem all along.
 * The problem ends up with the last init.
 */
suspend fun checkPlansForInits(
    domain: String, problem: String,
    initsToExpectedPlans: ExpressionsToTasks,
    searchPlan: PlanSearchFunction,
    logFunction: LogFunction
) {
    initsToExpectedPlans.forEach { initToExpectedPlan ->
        checkPlanForInit(
            domain,
            problem,
            initToExpectedPlan.first,
            searchPlan,
            logFunction,
            initToExpectedPlan.second
        )
    }
}

/**
 * Loads a domain & problem from a raw resources, and runs a planning function on it.
 * The result plan is returned.
 */
fun searchPlanFromResource(
    context: Context,
    resource: Int,
    searchPlan: PlanSearchFunction,
    logFunction: LogFunction
): List<Task> {
    val pddl = stringFromRawResource(context, resource)
    val (domain, problem) = splitDomainAndProblem(pddl)
    return runBlocking { searchPlan(domain, problem, logFunction) }
}

/**
 * Reads PDDL domain and problem from a raw resource, given its identifier.
 */
fun domainAndProblemFromRaw(context: Context, id: Int): Pair<String, String> {
    val pddl = stringFromRawResource(context, id)
    println("Using base PDDL: $pddl")
    return splitDomainAndProblem(pddl)
}

fun stringFromRawResource(context: Context, id: Int): String {
    val input = context.resources.openRawResource(id)
    return String(input.readBytes(), Charsets.UTF_8)
}
