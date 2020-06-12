package com.example.winpollgames.neo4j


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import github.etx.neo4j.DefaultNeoSerializer
import github.etx.neo4j.NeoLogging
import github.etx.neo4j.NeoQuery
import github.etx.neo4j.destruct
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Config
import org.neo4j.driver.v1.GraphDatabase



data class User(val name: String, val age: Int?)

class mainConnectionNeo4J {

        fun main() {
            println("ddddddddddddriver will start ")
            val driver = GraphDatabase.driver(
                "bolt://hobby-foheelocbfmfgbkebkmjjeel.dbs.graphenedb.com:24787",
                AuthTokens.basic("physiologygamemaster", "b.7llsVCCW6Ju4.QGZBgw7zxPb4OV4n")
//                , Config.build().withoutEncryption().toConfig()
            )
            println("ddddddddddddriver started ")

            val neo = NeoQuery(driver, DefaultNeoSerializer())
            println("nnnnnnnnnnnnnnn eo start")
            neo.submit("CREATE (u:User { name: {name}, age: {age} })", User("Alice", 18).destruct())
            neo.submit("CREATE (u:User { name: {name}, age: {age} })", User("Bob", null).destruct())
            println("nnnnnnnnnnnnnnn eo 2")
            val alice = neo.submit("MATCH (u:User { name:{name} }) RETURN u", mapOf("name" to "Alice"))
                .map { it.unwrap("u") }
                .map { User(it.string("name"), it.intOrNull("age")) }
                .singleOrNull()
            println("aaaaaaaaaaaaaliceASubmited ")

            println(alice) //User(name=Alice, age=18)

            //or
            val aliceB = neo.submit("MATCH (u:User { name:{name} }) RETURN u", mapOf("name" to "Alice"))
                .unwrap("u")
                .let {
                    User(it.string("name"), it.intOrNull("age"))
                }
            println(aliceB) //User(name=Alice, age=18)


            //You can use with Jackson
            val mapper = ObjectMapper().registerModule(KotlinModule())
            val users = neo.submit("MATCH (u:User) WHERE u.age IS NULL RETURN u")
                .map { it.unwrap("u") }
                .map { mapper.convertValue(it.asMap(), User::class.java) }
                .toList()
            println(users) //[User(name=Bob, age=null)]

            driver.close()
        }

}
