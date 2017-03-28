package se.lars.schema

import graphql.ExecutionResult
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.parameters.*
import graphql.language.Document
import graphql.validation.ValidationError


class ConsoleInstrumentation : Instrumentation {
    val executionList = mutableListOf<String>()
    class Timer<T> (val op: String, val executionList: MutableList<String>): InstrumentationContext<T> {
        private val start = System.currentTimeMillis()

        init {
           executionList += "start $op"
        }

        fun end()
        {
            val ms = System.currentTimeMillis () - start
            executionList += "end:$op in $ms ms"
        }

        override fun onEnd(result: T?) {
            end()
        }

        override fun onEnd(e: Exception) {
            end()
        }
    }

    override fun beginDataFetch(parameters: DataFetchParameters): InstrumentationContext<ExecutionResult> {
        return Timer("data-fetch", executionList)
    }

    override fun beginExecution(parameters: ExecutionParameters): InstrumentationContext<ExecutionResult> {
        return Timer("execution", executionList)
    }

    override fun beginField(parameters: FieldParameters): InstrumentationContext<ExecutionResult> {
        return Timer("field-${parameters.fieldDef.name}", executionList)
    }

    override fun beginFieldFetch(parameters: FieldFetchParameters): InstrumentationContext<Any> {
        return Timer("fetch-${parameters.fieldDef.name}", executionList)
    }

    override fun beginParse(parameters: ExecutionParameters): InstrumentationContext<Document> {
        return Timer("parse", executionList)
    }

    override fun beginValidation(parameters: ValidationParameters): InstrumentationContext<List<ValidationError>> {
        return Timer("validation", executionList)
    }
}