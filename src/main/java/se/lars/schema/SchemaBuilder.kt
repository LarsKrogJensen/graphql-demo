package se.lars.schema

import graphql.Scalars.*
import graphql.relay.Relay
import graphql.schema.*
import se.lars.schema.ScalarTypes.GraphQLDate
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.properties.Delegates
import kotlin.reflect.KClass


abstract class BuilderBase(val name: String) {
    var description: String? = null
}

abstract class TypeBuilderBase(name: String) : BuilderBase(name) {
    val fields: MutableList<GraphQLFieldDefinition<*>> = mutableListOf()

    inline fun <reified T : Any> field(name: String, block: FieldBuilder<T>.() -> Unit): Unit {
        val fieldBuilder = FieldBuilder<T>(name)
        fieldBuilder.type = typeResolve(T::class)
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


    inline fun <reified TArg : Any> argument(name: String, block: ArgumentBuilder<TArg>.() -> Unit): Unit {
        val argBuilder = ArgumentBuilder<TArg>(name)
        argBuilder.block()
        arguments += argBuilder.build()
    }

    inline fun <reified TArg : Any> argument(name: String): Unit {
        val argBuilder = ArgumentBuilder<TArg>(name)
        argBuilder.type = typeResolve(TArg::class)
        arguments += argBuilder.build()
    }

    fun build() = GraphQLFieldDefinition<T>(name,
                                            description,
                                            type,
                                            DataFetcher { dataFetcher?.invoke(it) },
                                            arguments,
                                            deprecationReason)
}

class InputFieldBuilder<T>(name: String) : BuilderBase(name) {
    var type: GraphQLInputType by Delegates.notNull<GraphQLInputType>()
    var defaultValue: T? = null

    fun build() = GraphQLInputObjectField(name,
                                          description,
                                          type,
                                          defaultValue)
}

class InterfaceBuilder(name: String) : TypeBuilderBase(name) {
    var typeResolver: TypeResolver? = null

    fun build() = GraphQLInterfaceType(name, description, fields, typeResolver)
}

class ObjectTypeBuilder(name: String) : TypeBuilderBase(name) {
    private val interfaces: MutableList<GraphQLInterfaceType> = mutableListOf()
    fun build() = GraphQLObjectType(name, description, fields, interfaces)
}

class ConnectionTypeBuilder(name: String) : BuilderBase(name) {
    var edgeType: GraphQLObjectType by Delegates.notNull<GraphQLObjectType>()
    //var nodeInterface: GraphQLInterfaceType? = null

    fun build() = Relay().connectionType(name, edgeType, listOf())
}

class EdgeTypeBuilder(name: String) : TypeBuilderBase(name) {
    var nodeType: GraphQLObjectType by Delegates.notNull<GraphQLObjectType>()
    var nodeInterface: GraphQLInterfaceType? = null

    fun build() = Relay().edgeType(name, nodeType, nodeInterface, fields)
}

class InputTypeBuilder(name: String) : BuilderBase(name) {
    val fields: MutableList<GraphQLInputObjectField> = mutableListOf()

    inline fun <reified T : Any> field(name: String, block: InputFieldBuilder<T>.() -> Unit): Unit {
        val fieldBuilder = InputFieldBuilder<T>(name)
        fieldBuilder.type = typeResolve(T::class)
        fieldBuilder.block()
        fields += fieldBuilder.build()
    }

    fun <T> field(field: GraphQLInputObjectField) {
        fields += field
    }
    fun build() = GraphQLInputObjectType(name, description, fields)
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

fun <T : Any> typeResolve(type: KClass<T>) = when (type) {
    String::class  -> GraphQLString
    Date::class    -> GraphQLDate
    Int::class     -> GraphQLInt
    Long::class    -> GraphQLLong
    Float::class   -> GraphQLFloat
    Double::class  -> GraphQLFloat
    Boolean::class -> GraphQLBoolean
    else           -> GraphQLString
}!!

fun graphqlType(name: String, block: ObjectTypeBuilder.() -> Unit) = ObjectTypeBuilder(name).apply { block() }.build()

fun graphqlInputType(name: String, block: InputTypeBuilder.() -> Unit) = InputTypeBuilder(name).apply { block() }.build()

fun <T> graphqlField(name: String, block: FieldBuilder<T>.() -> Unit) = FieldBuilder<T>(name).apply { block() }.build()

fun graphqlSchema(block: SchemaBuilder.() -> Unit) = SchemaBuilder().apply { block() }.build()

fun relayConnectionType(name: String, block: ConnectionTypeBuilder.() -> Unit) = ConnectionTypeBuilder(name).apply { block() }.build()

fun relayEdgeType(name: String, block: EdgeTypeBuilder.() -> Unit) = EdgeTypeBuilder(name).apply { block() }.build()

fun graphqlNonNull(wrapped: GraphQLType) = GraphQLNonNull(wrapped)

fun graphqlList(wrapped: GraphQLType) = GraphQLList(wrapped)

fun <T> succeeded(value: T) = CompletableFuture.completedFuture(value)

fun <T> succeededNullable(value: T?) = CompletableFuture.completedFuture(value)


fun <T> succeededOptional(optional: Optional<T>): CompletableFuture<T> {
    return succeeded(if (optional.isPresent) optional.get() else null)
}

fun succeededOptionalInt(optional: OptionalInt): CompletableFuture<Int> {
    return if (optional.isPresent) CompletableFuture.completedFuture(optional.asInt)
    else CompletableFuture.completedFuture(null)
}

