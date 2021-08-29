package io.github.jwgibanez.cartrack

import io.github.jwgibanez.cartrack.data.model.*

object TestUtil {

    fun createAccount(
        username: String, password: String, country: String
    ) = Account(
        username = username,
        password = password,
        country = country
    )

    fun createUser(id: Int): User = User(id= id)

    fun createAddress(): Address = Address()

    fun createGeo(): Geo = Geo()

    fun createCompany(): Company = Company()

    fun getUserList(): List<User> = listOf(
        createUser(1).apply {
            name = "Leanne Graham"
            username = "Bret"
            email = "Sincere@april.biz"
            address = createAddress().apply {
                street = "Kulas Light"
                suite = "Apt. 556"
                city = "Gwenborough"
                zipcode = "92998-3874"
                geo = createGeo().apply {
                    lat = "-37.3159"
                    lng = "81.1496"
                }
            }
            phone = "1-770-736-8031 x56442"
            website = "hildegard.org"
            company = createCompany().apply {
                name = "Romaguera-Crona"
                catchPhrase = "Multi-layered client-server neural-net"
                bs = "harness real-time e-markets"
            }
        },
        createUser(2).apply {
            name = "Ervin Howell"
            username = "Antonette"
            email = "Shanna@melissa.tv"
            address = createAddress().apply {
                street = "Victor Plains"
                suite = "Suite 879"
                city = "Wisokyburgh"
                zipcode = "90566-7771"
                geo = createGeo().apply {
                    lat = "-43.9509"
                    lng = "-34.4618"
                }
            }
            phone = "010-692-6593 x09125"
            website = "anastasia.net"
            company = createCompany().apply {
                name = "Deckow-Crist"
                catchPhrase = "Proactive didactic contingency"
                bs = "synergize scalable supply-chains"
            }
        },
        createUser(3).apply {
            name = "Clementine Bauch"
            username = "Samantha"
            email = "Nathan@yesenia.net"
            address = createAddress().apply {
                street = "Douglas Extension"
                suite = "Suite 847"
                city = "McKenziehaven"
                zipcode = "59590-4157"
                geo = createGeo().apply {
                    lat = "-68.6102"
                    lng = "-47.0653"
                }
            }
            phone = "1-463-123-4447"
            website = "ramiro.info"
            company = createCompany().apply {
                name = "Romaguera-Jacobson"
                catchPhrase = "Face to face bifurcated interface"
                bs = "e-enable strategic applications"
            }
        }
    )
}