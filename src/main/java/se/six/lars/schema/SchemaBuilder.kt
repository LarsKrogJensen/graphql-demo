package se.six.lars.schema

import graphql.Scalars.*
import graphql.schema.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.properties.Delegates
import kotlin.reflect.KClass


abstract class BuilderBase(val name: String) {
    var description: String? = null
}

abstract class TypeBuilderBase(name: String) : BuilderBase(name) {
    protected val fields: MutableList<GraphQLFieldDefinition<*>> = mutableListOf()

    inline fun <reified T : Any> field(name: String, block: FieldBuilder<T>.() -> Unit): Unit {
        val fieldBuilder = FieldBuilder<T>(name)
        val type: KClass<T> = T::class


        if (String::class == type) {
            fieldBuilder.type = GraphQLString
        } else if (Date::class == type) {
            fieldBuilder.type = ScalarTypes.GraphQLDate
        } else if (Int::class == type) {
            fieldBuilder.type = GraphQLInt
        } else if (Double::class == type) {
            fieldBuilder.type = GraphQLFloat
        } else if (Boolean::class == type) {
            fieldBuilder.type = GraphQLBoolean
        } else if (Float::class == type) {
            fieldBuilder.type = GraphQLFloat
        } else if (Long::class == type) {
            fieldBuilder.type = GraphQLLong
        }


        fieldBuilder.block()
        fields += fieldBuilder.build()
    }

    fun <T> field(field: GraphQLFieldDefinition<T>) {
        fields += field
    }
}

class ArgumentBuilder<T>(name: String) : BuilderBase(name) {
    var type: GraphQLInputType by Delegates.notNull<GraphQLInputType>()
    var defaultValue: T? = null

    fun build() = GraphQLArgument(name, description, type, defaultValue)
}

class FieldBuilder<T>(name: String) : BuilderBase(name) {
    var type: GraphQLOutputType by Delegates.notNull<GraphQLOutputType>()
    var dataFetcher: ((env: DataFetchingEnvironment) -> CompletionStage<T>)? = null
    val arguments: MutableList<GraphQLArgument> = mutableListOf()
    var deprecationReason: String? = null


    fun <TArg> argument(name: String, block: ArgumentBuilder<TArg>.() -> Unit): Unit {
        val argBuilder = ArgumentBuilder<TArg>(name)
        argBuilder.block()
        arguments += argBuilder.build()
    }

    fun build() = GraphQLFieldDefinition<T>(name,
                                            description,
                                            type,
                                            DataFetcher { dataFetcher?.invoke(it) },
                                            arguments,
                                            deprecationReason)
}

class InterfaceBuilder(name: String) : TypeBuilderBase(name) {
    var typeResolver: TypeResolver? = null

    fun build() = GraphQLInterfaceType(name, description, fields, typeResolver)
}

class TypeBuilder(name: String) : TypeBuilderBase(name) {

    private val interfaces: MutableList<GraphQLInterfaceType> = mutableListOf()
    fun build() = GraphQLObjectType(name, description, fields, interfaces)
}

class SchemaBuilder {
    var queryType: GraphQLObjectType? = null
    var mutationType: GraphQLObjectType? = null

    fun build(): GraphQLSchema {
        return GraphQLSchema.Builder()
                .query(queryType)
                .mutation(mutationType)
                .build()
    }
}

fun graphqlType(name: String, block: TypeBuilder.() -> Unit) = TypeBuilder(name).apply { block() }.build()

fun <T> graphqlField(name: String, block: FieldBuilder<T>.() -> Unit) = FieldBuilder<T>(name).apply { block() }.build()

fun graphqlSchema(block: SchemaBuilder.() -> Unit) = SchemaBuilder().apply { block() }.build()


fun graphqlNonNull(wrapped: GraphQLType) = GraphQLNonNull(wrapped)

fun graphqlList(wrapped: GraphQLType) = GraphQLList(wrapped)

fun <T> succeeded(value: T) = CompletableFuture.completedFuture(value)

fun <T> succeededOptional(optional: Optional<T>): CompletableFuture<T> {
    return succeeded(if (optional.isPresent) optional.get() else null)
}

fun succeededOptionalInt(optional: OptionalInt): CompletableFuture<Int> {
    return if (optional.isPresent) CompletableFuture.completedFuture(optional.asInt)
    else CompletableFuture.completedFuture(null)
}

