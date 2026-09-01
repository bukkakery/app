package com.example.data.db

import com.example.data.model.AttendanceEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.StaffEntity
import com.example.data.model.TestRecordEntity

object InitialDataProvider {

    fun getInitialShoot(): ShootEventEntity {
        return ShootEventEntity(
            shootCode = "20263108",
            date = "31/08/2026",
            time = "18:00",
            title = "Rodaje BUKKAKERY - Kitty Love",
            actressName = "Kitty Love",
            actressFee = 2000.0,
            actressPaymentMethod = "Transferencia bancaria",
            actressIsPaid = false,
            staffBudget = 690.0,
            expensesNotes = "Maquillador (60€), Seguridad/Limpieza (70€), Asistente (70€), Cámara (50€), Fotógrafo/CM (100€+100€ deuda), Scooper (150€), Dietas/Comida (80€), Emergencias (140€)",
            status = "EN_RODAJE",
            notes = "Total efectivo necesario: 2.190€ (1.710€ bukkakeros + 480€ staff en mano). Actriz 2.000€ por transferencia."
        )
    }

    fun getInitialStaff(shootCode: String = "20263108"): List<StaffEntity> {
        return listOf(
            StaffEntity(shootCode = shootCode, role = "Maquillador / Estilista / Peluquería", name = "Staff Maquillaje", fee = 60.0, isPaid = false),
            StaffEntity(shootCode = shootCode, role = "Seguridad / Limpieza", name = "Staff Seguridad", fee = 70.0, isPaid = false),
            StaffEntity(shootCode = shootCode, role = "Ayudante de Dirección / Producción (Asistente)", name = "Jamsen", fee = 70.0, isPaid = false),
            StaffEntity(shootCode = shootCode, role = "Operador de Cámara B", name = "Cámara B", fee = 50.0, isPaid = false),
            StaffEntity(shootCode = shootCode, role = "Fotógrafo / CM", name = "Fotógrafo Pro", fee = 200.0, isPaid = false, notes = "100€ rodaje + 100€ pendiente sábado"),
            StaffEntity(shootCode = shootCode, role = "Scooper", name = "Scooper Lead", fee = 150.0, isPaid = false),
            StaffEntity(shootCode = shootCode, role = "Dietas / Comida previstas", name = "Catering / Bebidas", fee = 80.0, isPaid = true),
            StaffEntity(shootCode = shootCode, role = "Fondo de Emergencias", name = "Caja Imprevistos", fee = 140.0, isPaid = false)
        )
    }

    fun getInitialParticipants(): List<ParticipantEntity> {
        return listOf(
            ParticipantEntity(1, "DNI", "51558666A", "608486", "06/07/2026", "NEGATIVO", "3", "JONATHAN ROSADO", "JONATHAN ALFREDO ROSADO CEDEÑO", "640217912", "", "MIRCO", "JONTRAALFREDO363@GMAIL.COM", "jhonnyk7", "13/10/1990", "No renovar", 40, 3, 3, "", "", "20263108", false),
            ParticipantEntity(2, "DNI", "49589580Q", "700293", "31/07/2026", "NEGATIVO", "3", "TEMPLARIO", "DAVID ANDRES LLUMIQUINGA BANDA", "655071474", "", "MIRCO", "DAVID.RASTA@LIVE.COM", "Davideltemplario", "5/5/1997", "No renovar", 0, 3, 3, "", "", "20263108", true),
            ParticipantEntity(3, "NIE", "Z2103315Q", "700270", "30/07/2026", "NEGATIVO", "5", "SINNERLEO", "NILLER APARICIO PRIETO", "610402984", "", "MIRCO", "LEANDRO.APARICIO2018@GMAIL.COM", "sinnerleo_0", "17/04/2000", "Renovar gratis", 70, 5, 6, "", "", "20263108", true),
            ParticipantEntity(4, "NIE", "Y7644424C", "700367", "14/08/2026", "NEGATIVO", "2", "MARTIN VERA", "ALBERTO MARTIN BORJA VERANO", "602564110", "", "MIRCO", "ALBEMAR61@GMAIL.COM", "ALBERTO40", "7/9/1979", "Renovar gratis", 60, 2, 6, "", "", "20263108", false),
            ParticipantEntity(5, "DNI", "54692426J", "606833", "29/06/2026", "NEGATIVO", "N", "JESUS C", "Gonzalez Carrasco, Jesús Aníbal", "617510302", "", "MIRCO", "JESUS123@GMAIL.COM", "CARLOS1204J", "12/4/1970", "No renovar", 20, 1, 2, "", "", "20263108", false),
            ParticipantEntity(6, "DNI", "49452811M", "700470", "31/08/2026", "NEGATIVO", "1", "DIEGO M", "VILLALVA AGUILAR, DIEGO GERAR", "611467059", "", "MIRCO", "thelilpewee360@gmail.com", "DIEGOM0000", "16/7/1991", "Renovar gratis", 0, 1, 6, "", "", "20263108", false),
            ParticipantEntity(7, "DNI", "09052921Y", "700048", "22/06/2026", "NEGATIVO", "2", "LIMON", "RODRIGUEZ MARTIN, ADRIAN", "699690402", "", "MIRCO", "limonrm88@hotmail.com", "LIMONACIDO", "15/11/1988", "Renovar gratis", 0, 2, 7, "", "", "20263108", true),
            ParticipantEntity(8, "DNI", "49097011S", "700265", "29/07/2026", "NEGATIVO", "3", "Parko", "DAVID BELTRAN RODRIGUEZ", "695986142", "", "MIRCO", "artranbel@gmail.com", "PARKO_PARADISE", "28/10/1994", "Renovar gratis", 40, 3, 8, "", "", "20263108", true),
            ParticipantEntity(9, "NIE", "Y5808954L", "700450", "28/08/2026", "NEGATIVO", "1", "REX", "VARGAS LEMUS, CRISTIAN ANDRIA", "677764842", "ES81 0182 1345 3902 0173 1274", "MIRCO", "rex@mail.com", "REXXX27", "27/4/1989", "Renovar gratis", 0, 1, 6, "", "", "20263108", true),
            ParticipantEntity(10, "DNI", "47946881T", "0", "", "PENDIENTE", "ACTIVO", "CONSTAN", "GUILLERMO CONSTAN BULNES", "638190023", "", "MIRCO", "GCONSTANBULNES@YAHOO.ES", "Jcarlos19951", "", "No renovar", 0, 0, 0, "", "", "20263108", false),
            ParticipantEntity(11, "NIE", "Y9648696A", "700396", "20/08/2026", "NEGATIVO", "4", "EDUARDO ALFREDO", "EDUARDO VALBUENA TORRES", "722218065", "", "MIRCO", "EVALTOR1994@GMAIL.COM", "WARNEX1990", "4/11/1994", "Renovar gratis", 40, 4, 7, "", "", "20263108", true),
            ParticipantEntity(12, "DNI", "03535707D", "700456", "31/08/2026", "NEGATIVO", "3", "ESTU", "ESTUARDO PARRA ESCOBAR", "681285055", "", "MIRCO", "stu.espe@hotmail.com", "EAPR26", "15/8/1974", "Renovar gratis", 0, 3, 6, "", "", "20263108", true),
            ParticipantEntity(13, "DNI", "70418831F", "700468", "31/08/2026", "NEGATIVO", "3", "OSCAR GIL", "OSCAR GIL ESCOBAR", "647209281", "", "MIRCO", "ferrero1324@hotmail.com", "OSCARGIL", "6/10/1992", "Renovar gratis", 0, 3, 6, "", "", "20263108", false),
            ParticipantEntity(14, "DNI", "02268938B", "609250", "09/06/2026", "NEGATIVO", "1", "Alberto MADRIZZZ", "ALBERTO LORANCA SERRA", "657509376", "", "MIRCO", "AALBERTO884@GMAIL.COM", "ALBERTOMADRIZ", "4/11/1978", "Renovar gratis", 20, 1, 5, "", "", "20263108", true),
            ParticipantEntity(19, "DNI", "53474566A", "609896", "13/07/2026", "NEGATIVO", "4", "JAP", "Anaya Pérez, Joel Clemente", "615247324", "", "MIRCO", "joeelanaya@gmail.com", "joelgetafe", "8/11/1994", "Renovar gratis", 60, 4, 6, "", "", "20263108", false),
            ParticipantEntity(21, "DNI", "05450254J", "700294", "31/07/2026", "NEGATIVO", "2", "ALEXZT", "ZARATE GUANOLISA, ALEXANDER", "696001504", "ES6315632626383266538020", "MIRCO", "alexander.zte1@gmail.com", "PORTU4NO", "16/10/1994", "Renovar gratis", 20, 2, 5, "", "", "20263108", false),
            ParticipantEntity(22, "NIE", "Y8656375V", "700334", "06/08/2026", "NEGATIVO", "3", "MAIC", "BLAIN, MICHEL DAVID", "642510189", "", "MIRCO", "michelblain750@gmail.com", "MICHEL7500", "30/6/1999", "Renovar gratis", 0, 3, 6, "", "", "20263108", true),
            ParticipantEntity(25, "DNI", "51224231B", "0", "", "PENDIENTE", "BANEADO", "NICOLAS SEGUNDO", "ELLIOT", "681045496", "", "MIRCO", "", "FRANCESC8080", "9/6/1995", "No renovar", 0, 0, 0, "", "Usuario baneado por conducta", "20263108", false),
            ParticipantEntity(26, "DNI", "51095593N", "700427", "27/08/2026", "NEGATIVO", "2", "JMM", "JOSE MILLAN MORENO", "649493321", "", "MIRCO", "torcpi@gmail.com", "TORCUATO", "23/4/1998", "Renovar gratis", 0, 2, 5, "", "", "20263108", false),
            ParticipantEntity(27, "NIE", "51274693B", "700329", "05/08/2026", "NEGATIVO", "3", "LUIS", "LUYGY HUXEL BAZAN GARCIA", "610551085", "", "MIRCO", "LUYGYGARCIABA@OUTLOOK.COM", "LUISG66", "13/07/1992", "Renovar gratis", 0, 3, 5, "", "", "20263108", true),
            ParticipantEntity(28, "DNI", "70047129P", "607287", "14/07/2026", "NEGATIVO", "1", "ACTIONMAN", "PABLO GIMENO MENDEZ", "649993201", "", "MIRCO", "billytodresto@gmail.com", "ACTIONMANBILLY", "18/05/1973", "Renovar gratis", 0, 1, 5, "", "", "20263108", false),
            ParticipantEntity(33, "DNI", "16853053X", "609902", "08/06/2026", "NEGATIVO", "BANEADO", "SNIPER ( RONNYGC8)", "RONNY EDUARDO GONZALEZ CABRERA", "646794588", "", "MIRCO", "ronny.gc@outlook.com", "RonnyGC8", "04/03/1997", "No renovar", 0, 0, 1, "", "Baneado permanentemente", "20263108", false),
            ParticipantEntity(34, "NIE", "X5411923T", "700269", "30/07/2026", "NEGATIVO", "3", "DMRC", "DANNY MAURICIO RAMOS CADENA", "677038226", "ES58 2100 1908 7302 0029 6445", "MIRCO", "Dmrc222017@gmail.con", "DMRC96", "09/04/1996", "Renovar gratis", 20, 3, 7, "", "", "20263108", true),
            ParticipantEntity(35, "DNI", "50775320Z", "606729", "22/06/2026", "NEGATIVO", "1", "DANY BOY", "DANIEL CALO PLASENCIA", "6636213773", "", "MIRCO", "Danielc.plasencia@gmail.com", "DnyWby", "", "Renovar gratis", 40, 1, 5, "", "", "20263108", false),
            ParticipantEntity(36, "DNI", "55106140A", "700304", "03/08/2026", "NEGATIVO", "4", "PETER - PEDRO", "PEDRO MANUEL PARIS LUACES", "603426988", "", "MIRCO", "pedrom2708@gmail.com", "PeroParisLol", "27/08/1996", "Renovar gratis", 40, 4, 8, "", "", "20263108", true),
            ParticipantEntity(60, "DNI", "06019634M", "700398", "31/08/2026", "NEGATIVO", "6", "TAZMANIA", "JOSUE SAN PEDRO", "663462703", "", "MIRCO", "dani_dada19@hotmail.com", "tazmania", "", "Renovar gratis", 0, 6, 8, "", "", "20263108", true),
            ParticipantEntity(61, "DNI", "51718012M", "700415", "25/08/2026", "NEGATIVO", "7", "DORIAN BLACK", "CAMINO SALAN OSCAR MIGUEL", "669309752", "", "MIRCO", "Miguel_2526@hotmail.com", "blackdoriann", "", "Renovar gratis", 0, 7, 9, "", "", "20263108", true),
            ParticipantEntity(63, "DNI", "02253757X", "700434", "27/08/2026", "NEGATIVO", "7", "EMPOTRADOR", "CASADO GARCIA SERGIO", "646803785", "ES2200496121402716396386", "MIRCO", "sergicasado111@hotmail.com", "Sergioc69", "", "Renovar gratis", 40, 7, 10, "", "", "20263108", true),
            ParticipantEntity(66, "DNI", "52027783N", "700328", "05/08/2026", "NEGATIVO", "4", "VyperMc", "MONTERO CUEVA BRYAN STEVEN", "657103617", "ES79 2100 2650 8313 0018 7358", "MIRCO", "Bryanmcueva05@gmail.com", "Vyper.9", "05/06/1998", "Renovar gratis", 40, 4, 8, "", "", "20263108", true),
            ParticipantEntity(69, "DNI", "54366241Z", "700465", "31/08/2026", "NEGATIVO", "3", "SEBAS", "BEJARANO MANTILLA SEBASTIÁN", "624495401", "", "MIRCO", "s.bejaranomantila@gmail.com", "sebas09pb", "", "Renovar gratis", 20, 3, 5, "", "", "20263108", false),
            ParticipantEntity(70, "DNI", "06287769Y", "606625", "16/06/2026", "NEGATIVO", "BANEADO", "SITO", "Luis Rafael Molina Cañamero", "", "", "MIRCO", "", "", "25/02/1996", "No renovar", 0, 0, 0, "", "Baneado", "20263108", false),
            ParticipantEntity(73, "DNI", "51006566H", "700030", "18/06/2026", "NEGATIVO", "1", "Adriano Prive", "Adrián armenteros Camacho", "", "", "LUXOR", "", "Adrian_pride", "", "No renovar", 80, 1, 4, "", "", "20263108", false),
            ParticipantEntity(82, "DNI", "48804884B", "607017", "23/06/2026", "NEGATIVO", "1", "Cronos", "Gonzalo Aguilera bayon", "662360072", "", "MEGAN", "gonzaloaguilerabayon@gmail.com", "gonzaloo616", "", "No renovar", 60, 1, 3, "", "", "20263108", false),
            ParticipantEntity(86, "DNI", "51425466L", "700395", "20/08/2026", "NEGATIVO", "BANEADO", "MARIO LUXOR", "FRANCISCO MARIO DE LAS HERAS ALARCÓN", "609443977", "", "LUXOR", "francisco@dominionaudiovisual.com", "@Marioluxor", "", "No renovar", 20, 0, 3, "", "Baneado", "20263108", false),
            ParticipantEntity(91, "NIE", "Z4571938A", "606819", "26/06/2026", "NEGATIVO", "3", "Mediterranean Stallion", "Niccolò Annicchiarico", "614213905", "", "MIRCO", "nicculecchia@gmail.com", "@medStallion", "", "No renovar", 20, 3, 4, "", "", "20263108", false),
            ParticipantEntity(92, "NIE", "Y9809955D", "700259", "28/07/2026", "NEGATIVO", "4", "GIO", "Geovanny Adonis Jiron Bordas", "641576122", "", "MIRCO", "adonisjibor@gmail.com", "@giojibor", "", "Renovar gratis", 40, 4, 7, "", "", "20263108", true),
            ParticipantEntity(94, "NIE", "X6678201J", "700464", "31/08/2026", "NEGATIVO", "2", "NOHAJ", "Johann Mertens", "666834928", "", "MIRCO", "noahaj78@gmail.com", "@noahaj", "", "Renovar gratis", 110, 2, 5, "", "", "20263108", false),
            ParticipantEntity(96, "DNI", "01176149k", "700449", "28/08/2026", "NEGATIVO", "4", "ROMANONES", "David Dávila Román", "600313365", "", "MIRCO", "ddavilaroman@hotmail.com", "@romanones", "", "Renovar gratis", 20, 4, 7, "", "", "20263108", true),
            ParticipantEntity(99, "DNI", "06030071T", "700312", "04/08/2026", "NEGATIVO", "1", "Santi Harzuka", "GUILLERMO SANTILLAN", "644723407", "", "HARZUKA", "guillerivas12@hotmail.com", "Santi_dst", "", "Renovar gratis", 140, 1, 6, "", "", "20263108", false),
            ParticipantEntity(100, "DNI", "47281944Z", "700057", "22/06/2026", "POSCLAMIDA", "0", "Calimero", "ALEJANDRO JOSE GENDE", "613860581", "", "MIRCO", "", "", "", "No renovar", 0, 0, 2, "", "Resultado positivo clamidia", "20263108", false),
            ParticipantEntity(101, "DNI", "X9712213A", "700366", "14/08/2026", "NEGATIVO", "3", "Acartonado", "Carlos Ivan de la Cruz Méndez", "632266543", "", "MIRCO", "carlos_ivan_0@hotmail.com", "Calimeritoo", "", "Renovar gratis", 0, 3, 6, "", "", "20263108", true),
            ParticipantEntity(102, "DNI", "60429531K", "700287", "31/07/2026", "NEGATIVO", "3", "CHRISTHOPER", "DURAN PEÑA CRISTHIAN CAMILO", "657382902", "", "MIRCO", "cduran498@outlook.es", "Christopher912", "", "Renovar gratis", 40, 3, 6, "", "", "20263108", true),
            ParticipantEntity(104, "DNI", "CA12689AP", "607211", "07/07/2026", "NEGATIVO", "1", "LEONARDO", "DARÍO CELESTE", "644293883", "", "PEPE POLLA", "CELESTEDARIO@GMAIL.COM", "@vct_sfs", "", "No renovar", 30, 1, 2, "", "", "20263108", false),
            ParticipantEntity(106, "DNI", "08868054J", "700088", "29/06/2026", "NEGATIVO", "BANEADO", "GUIRI", "Federico Narváez Thomas", "654806562", "", "MIRCO", "fnthomas2000@yahoo.es", "@fellini2000", "", "No renovar", 20, 0, 1, "", "Baneado", "20263108", false),
            ParticipantEntity(107, "NIE", "Y7441835S", "700140", "13/07/2026", "NEGATIVO", "2", "AJOSHEP", "ARNOLD JOSHEP IRIAS RODRÍGUEZ", "651615934", "", "PARKO", "arnoldjoshep23.ajir@gmail.com", "AJIR1110", "", "No renovar", 20, 2, 3, "", "", "20263108", false),
            ParticipantEntity(108, "DNI", "07137118B", "700467", "31/08/2026", "NEGATIVO", "2", "ADRIANO", "GERARDO MEZA DÍAZ", "600485100", "", "LUIS", "GERARDOMESADIAZ@GMAIL.COM", "@gerardomd", "", "Renovar gratis", 20, 2, 5, "", "", "20263108", false),
            ParticipantEntity(111, "DNI", "02625266T", "700351", "11/08/2026", "NEGATIVO", "3", "EL ARQUITECTO", "JAVIER FRANCISCO BERMEJO CASTRO", "626001026", "", "MIRCO", "Jv200fotografia@gmail.com", "@JvrMdrd", "", "Renovar gratis", 20, 3, 6, "", "", "20263108", true),
            ParticipantEntity(112, "DNI", "50231202F", "700299", "31/07/2026", "NEGATIVO", "BANEADO", "SOLONE", "Carlos Augusto Álvarez reyes", "699662628", "", "STU", "solrachpk@gmail.com", "So_lo_ne", "", "No renovar", 20, 1, 4, "", "Baneado", "20263108", false),
            ParticipantEntity(115, "NIE", "Z3885537Z", "700298", "31/07/2026", "NEGATIVO", "3", "CHINO MORENO", "WILMER ALEXANDER GARCIA GARCIA", "658147586", "", "MIRCO", "willgar123456@gmail.com", "chinomoreno66", "", "Renovar gratis", 40, 3, 6, "", "", "20263108", true),
            ParticipantEntity(124, "DNI", "49143596W", "700380", "18/08/2026", "NEGATIVO", "4", "RIAZA", "RIAZA LUNA DAVID", "625952422", "", "MIRCO", "davidriazaluna@gmail.com", "RIAZA22", "", "Renovar gratis", 60, 4, 6, "", "", "20263108", false),
            ParticipantEntity(127, "DNI", "04239219C", "607232", "08/07/2026", "NEGATIVO", "0", "JHON", "JONATHAN FERNANDO", "653376385", "", "PARKO", "fernandofch019@gmail.com", "JOHN01_9", "", "No renovar", 20, 0, 1, "", "", "20263108", false),
            ParticipantEntity(128, "PASAPORTE", "194414728", "700336", "06/08/2026", "NEGATIVO", "4", "YOHANDER", "Yohander Rafael ibrial pargas", "610790825", "ES49 0182 7277 1202 0164 6772", "ANGEL", "yohanderpargas@gmail.com", "@Alejop23", "", "Renovar gratis", 0, 4, 7, "", "", "20263108", true),
            ParticipantEntity(131, "DNI", "26520973H", "700346", "10/08/2026", "NEGATIVO", "3", "PIT", "Pedro Ángel Chicharro rodriguez", "640146358", "", "REBECCA", "pedroangelchicharro@gmail.com", "@pedroangelx", "", "Renovar gratis", 0, 3, 6, "", "", "20263108", true),
            ParticipantEntity(132, "NIE", "Y8462628E", "700292", "31/07/2026", "NEGATIVO", "5", "SANSON", "Joaquín taica rabanal", "663424968", "es6114650100911774498599", "SOLONE", "taicajoaquin53@gmail.com", "@djjoaquinrabanal", "", "Renovar gratis", 40, 5, 8, "", "", "20263108", true),
            ParticipantEntity(134, "DNI", "51141439L", "700137", "13/07/2026", "NEGATIVO", "BANEADO", "Sr. MF", "ADRIAN MASEDA", "650641174", "", "PARKO", "ad_mfm@hotmail.com", "adri_09i", "", "No renovar", 0, 0, 2, "", "Baneado", "20263108", false),
            ParticipantEntity(136, "DNI", "54712155P", "700147", "14/07/2026", "NEGATIVO", "BANEADO", "Eddy Santiago", "Eddy Santiago macana castellanos", "6418701198", "", "INSTAGRAM", "macanasantiago04@gmail.com", "@eddigonza", "", "No renovar", 20, 0, 1, "", "Baneado", "20263108", false),
            ParticipantEntity(137, "NIE", "Z4828960T", "700445", "28/08/2026", "NEGATIVO", "0", "Mario1117", "Mario Andrés Bedoya", "671299717", "", "ANGEL", "mario.bedoya@hotmail.com", "@mariobedoya1117", "", "Renovar gratis", 20, 0, 5, "", "", "20263108", false),
            ParticipantEntity(138, "DNI", "53660079K", "700386", "13/07/2026", "NEGATIVO", "4", "El Oso", "Álvaro nordman", "611051532", "", "ANGEL", "nordmanalvaro@gmail.com", "@heisenberg", "", "Renovar gratis", 0, 4, 6, "", "", "20263108", true),
            ParticipantEntity(141, "DNI", "53666527Y", "609878", "13/07/2026", "NEGATIVO", "1", "GONZA", "Gonzalo Estepa Rubio", "605222199", "", "PARKO", "gonzaloesteparubio@gmail.com", "@gutZV", "", "No renovar", 20, 1, 2, "", "", "20263108", false),
            ParticipantEntity(143, "DNI", "51136531X", "700301", "31/07/2026", "NEGATIVO", "1", "LUIS87", "Luis alberto suntaxi acosta", "650178625", "", "INSTAGRAM", "luissunt@gmail.com", "@luiss1987", "", "Renovar gratis", 80, 1, 5, "", "", "20263108", false),
            ParticipantEntity(147, "DNI", "05975131F", "700196", "20/07/2026", "NEGATIVO", "1", "Aqua gallardo", "Román niño gallardo", "667317254", "", "Eddy Gonza", "ninogallardoroma@gmail.com", "@aquagallardo", "", "No renovar", 20, 1, 2, "", "", "20263108", false),
            ParticipantEntity(149, "DNI", "51107194K", "700190", "20/07/2026", "NEGATIVO", "0", "José Carreño", "Luis Suárez Carreño", "681935643", "", "Yohander", "luis-scm@hotmail.com", "@LuisSM26", "", "No renovar", 20, 0, 1, "", "", "20263108", false),
            ParticipantEntity(151, "NIE", "Z2482669P", "700155", "16/07/2026", "NEGATIVO", "0", "ElTrigueño", "José Luis Martínez", "603356049", "", "MIRCO", "martinezmolinaj762@gmail.com", "@eltrig", "", "No renovar", 20, 0, 1, "", "", "20263108", false),
            ParticipantEntity(152, "DNI", "49095398N", "700154", "16/07/2026", "NEGATIVO", "0", "GORRAS", "Miguel Ángel Ruiz Rodríguez", "", "", "PARKO", "", "", "", "No renovar", 95, 0, 3, "", "", "20263108", false),
            ParticipantEntity(153, "PASAPORTE", "124617338", "700159", "16/07/2026", "NEGATIVO", "2", "ZETA", "LUIS BRYAN ZAPATA ASTUVILCA", "667020887", "", "JAMSEN", "Lbryan.zeta.23@gmail.com", "", "", "No renovar", 20, 2, 3, "", "", "20263108", false),
            ParticipantEntity(156, "DNI", "34898488J", "607352", "17/07/2026", "NEGATIVO", "0", "Bryan", "Javier roca dorado", "637218373", "", "Página web", "princechristianmillerxxx@gmail.com", "@javi", "", "No renovar", 75, 0, 3, "", "", "20263108", false),
            ParticipantEntity(157, "DNI", "53709833A", "700186", "20/07/2026", "NEGATIVO", "0", "Jesus correa", "Miguel Angel Acebo Gallego", "610467232", "", "Fetlife", "miguelangelacebo@gmail.com", "@miguelangelacebo", "", "No renovar", 45, 0, 2, "", "Hacer transferencia con balance", "20263108", false),
            ParticipantEntity(160, "PASAPORTE", "125859369", "700193", "20/07/2026", "NEGATIVO", "0", "JEMIE", "Jemie Arnaldo Francia Sanchez", "602013364", "", "YOHANDER", "jemiefrancia@gmail.com", "@maxor2", "", "Renovar gratis", 40, 0, 5, "", "", "20263108", true),
            ParticipantEntity(163, "NIE", "Z2116577F", "700183", "20/07/2026", "NEGATIVO", "0", "AXEL", "Ebel Cedeño", "689284699", "", "MIRCO", "ebelmadrid@gmail.com", "EJCM11", "", "No renovar", 20, 0, 1, "", "", "20263108", false),
            ParticipantEntity(164, "NIE", "X8108592B", "700194", "20/07/2026", "NEGATIVO", "0", "RUIGER", "Germán García Robalino", "640508001", "", "MIRCO", "garciarobalinogerman@gmail.com", "@Ruiger1992", "", "No renovar", 60, 0, 2, "", "", "20263108", false),
            ParticipantEntity(165, "NIE", "Z5024918K", "608310", "21/07/2026", "NEGATIVO", "0", "Edi-thor", "Jesus walter Manuel Gutierrez Ypanaque", "624855689", "", "MIRCO", "guttyfilms25f@gmail.com", "@Gzus_f", "", "No renovar", 0, 0, 1, "", "", "20263108", false),
            ParticipantEntity(166, "PASAPORTE", "124222860", "700200", "22/07/2026", "NEGATIVO", "0", "Chuky", "Elvis Retuerto Moreno", "", "", "Sugartatto", "", "", "", "No renovar", 100, 0, 4, "", "", "20263108", true),
            ParticipantEntity(171, "DNI", "53048580T", "700226", "23/07/2026", "NEGATIVO", "1", "ESTEBITAN", "Esteban Esteban Grande", "675722326", "", "PARKO", "estebanestebangrande@hotmail.com", "@estebitan43", "", "No renovar", 0, 1, 2, "", "", "20263108", false),
            ParticipantEntity(172, "PASAPORTE", "BF627618", "700227", "23/07/2026", "NEGATIVO", "1", "Eli", "ELIECER PRADA", "663069159", "", "YOHANDER", "elipj404@gmail.com", "", "", "Renovar gratis", 20, 1, 5, "", "", "20263108", false),
            ParticipantEntity(174, "DNI", "52986967G", "700466", "31/08/2026", "NEGATIVO", "3", "LOGAN", "JOSE DAVID RONQUILLO CANTÓN", "680117117", "", "PARKO", "josedavidronquillocanton@gmail.com", "@DavidLogan3", "", "Renovar gratis", 0, 3, 6, "", "", "20263108", true),
            ParticipantEntity(175, "NIE", "Y0800187P", "700264", "29/07/2026", "NEGATIVO", "2", "CHAVAL77", "RODEL ANDREI GUDULAI", "658227285", "ES32 1583 0001 1490 2376 3712", "PARKO", "andreirodel00@gmail.com", "@nose tuveras", "", "Renovar gratis", 0, 2, 5, "", "", "20263108", true),
            ParticipantEntity(178, "DNI", "51128553J", "700471", "31/08/2026", "NEGATIVO", "2", "MANU", "GUIILLERMO LARA", "665866329", "", "PARKO", "guillo.011204@gmail.com", "@SLMG04", "", "Renovar analíticas", 0, 2, 5, "", "", "20263108", true),
            ParticipantEntity(181, "DNI", "E03153360", "700248", "27/07/2026", "NEGATIVO", "0", "Denis Ariel", "Denis Ariel Romero Villanurva", "614766202", "", "YOHANDER", "DENISROMERO9999@GMAIL.COM", "", "", "No renovar", 0, 0, 1, "", "", "20263108", false),
            ParticipantEntity(182, "PASAPORTE", "123060605", "700243", "27/07/2026", "NEGATIVO", "1", "Álvarez", "Carlos Jhoan Álvarez parado", "642891445", "", "YOHANDER", "cj_2906@hotmail.com", "@you_AZ2", "", "No renovar", 20, 1, 3, "", "", "20263108", true),
            ParticipantEntity(185, "NIE", "Z0303124N", "700308", "03/08/2026", "NEGATIVO", "2", "Don jei", "CASTILLEJO PATERNINA Jeyson Rafael", "686805923", "", "Luis", "", "sonjei1", "", "No renovar", 40, 2, 4, "", "", "20263108", true),
            ParticipantEntity(186, "DNI", "49441055W", "700247", "27/07/2026", "NEGATIVO", "0", "GINGER", "Ginés Cárceles Sánchez", "633273023", "", "Angel", "gines-8@hotmail.com", "", "", "No renovar", 0, 0, 1, "", "", "20263108", false),
            ParticipantEntity(188, "DNI", "53815668S", "700266", "29/07/2026", "NEGATIVO", "4", "Asianwc", "Sebastián Rodríguez", "679538340", "", "Harzuka", "dannybrisck@gmail.com", "@bimmerm57", "", "Renovar gratis", 120, 4, 8, "", "", "20263108", true),
            ParticipantEntity(191, "DNI", "49994311Q", "700317", "04/08/2026", "NEGATIVO", "3", "TONY", "Carlos Andres Rengifo Andrade", "640281433", "", "PARKO", "tonyactorcontacto@gmail.com", "@charly0renjo", "", "Renovar gratis", 60, 3, 6, "", "", "20263108", true),
            ParticipantEntity(192, "NIE", "X8507106G", "606846", "31/07/2026", "NEGATIVO", "1", "Tigre", "Juan Sebastián Zúñiga Murcia", "699021111", "", ".", "sebstianzm773099@yahoo.com", "@sebas730", "", "No renovar", 0, 1, 2, "", "", "20263108", false),
            ParticipantEntity(193, "DNI", "09065786Z", "700284", "31/07/2026", "NEGATIVO", "2", "Gamarra", "Carlos Gamarra marcos", "676217981", "", ".", "scarlosbronzx@gmail.com", "@marcoscrisp74", "", "Renovar gratis", 20, 2, 5, "", "", "20263108", false),
            ParticipantEntity(196, "DNI", "61787572G", "700307", "03/08/2026", "NEGATIVO", "BANEADO 3 (VERRUGA)", "ANDRÉS", "ANDRES EDUARDO VIRRUETA MARQUEZ", "671220118", "", "PARKO", "andres.virruetamarquez@gmail.com", "@AndresssMx", "", "No renovar", 0, 0, 1, "", "Baneado temporalmente por verruga", "20263108", false),
            ParticipantEntity(197, "DNI", "49140320S", "700438", "28/08/2026", "NEGATIVO", "4", "PHOENIX", "Daniel Fornieles Tercero", "633520203", "", "Asían bw", "danielforte98@gmail.com", "@phoenix", "", "Renovar gratis", 0, 4, 7, "", "", "20263108", true),
            ParticipantEntity(202, "DNI", "76253096T", "700339", "07/08/2026", "NEGATIVO", "1", "Romano", "Jesús García Prida", "685519577", "", "Asían bw", "jesusggp_21@hotmail.com", "jeusgp", "", "No renovar", 20, 1, 2, "", "", "20263108", false),
            ParticipantEntity(203, "DNI", "47457878T", "700361", "14/08/2026", "POSITIVO CHLAMIDIA", "BANEADO", "Platino", "Javier Dorado", "", "", "Asían bw", "", "", "", "No renovar", 0, 0, 1, "", "Baneado por resultado positivo", "20263108", false),
            ParticipantEntity(206, "DNI", "05422603P", "700352", "11/08/2026", "NEGATIVO", "3", "Kardark", "Carlos Grande", "666755335", "", "Asían bw", "carlos_grande_s@hotmail.com", "@kgs2025", "", "Renovar gratis", 0, 3, 6, "", "", "20263108", true),
            ParticipantEntity(207, "NIE", "Z4548886C", "700344", "10/08/2026", "NEGATIVO", "2", "ALESSANDRO", "GABRIEL ALEJANDRO CASTAÑO", "683608690", "", "PARKO", "alejo160712@gmail.com", "@Alejandro1607", "", "Renovar gratis", 20, 2, 5, "", "", "20263108", true),
            ParticipantEntity(208, "DNI", "11852166J", "700350", "10/08/2026", "NEGATIVO", "3", "Youther", "Arturo", "622932703", "", "Asían BW", "arturo.serranopt@gmail.com", "@youtherag", "", "Renovar gratis", 0, 3, 5, "", "", "20263108", true),
            ParticipantEntity(210, "DNI", "05461302K", "700357", "13/08/2026", "NEGATIVO", "1", "ROB", "Fernando Pérez de Miguel", "640188550", "", "Luis", "ferpuuu1@gmail.com", "mdfer8", "", "No renovar", 20, 1, 2, "", "", "20263108", false),
            ParticipantEntity(211, "DNI", "51012422D", "700379", "18/08/2026", "NEGATIVO", "2", "Rubio", "David rubio sanchez", "", "", "Stella novax", "", "", "", "No renovar", 80, 2, 4, "", "", "20263108", false),
            ParticipantEntity(212, "DNI", "61387548L", "700387", "19/08/2026", "NEGATIVO", "1", "RICHI69", "Ricardo noel huanca soto", "642977055", "", "Mirco P", "ricardo.richy619@gmail.com", "", "", "No renovar", 20, 1, 2, "", "", "20263108", true),
            ParticipantEntity(214, "DNI", "60570959E", "700385", "19/08/2026", "POSITIVO CHLAMIDIA", "BANEADO", "Fidel vivas", "Fidel vivas campos", "", "", "Kitty Love", "", "", "", "No renovar", 0, 0, 1, "", "Baneado por resultado positivo", "20263108", false),
            ParticipantEntity(216, "NIE", "Z0443810F", "700394", "20/08/2026", "NEGATIVO", "1", "MAIKOL", "Maikoll Maldonado Reyes", "693738986", "", "Kitty Love", "Maikolmaldonado9@gmail.com", "@mixologinart", "", "No renovar", 20, 1, 2, "", "", "20263108", true),
            ParticipantEntity(219, "DNI", "47534809L", "700416", "25/08/2026", "NEGATIVO", "1", "FORGACS", "René Forgacs Pastor", "661393369", "", "PARKO", "renforgacs@gmail.com", "@forgacs", "", "No renovar", 20, 1, 3, "", "", "20263108", true),
            ParticipantEntity(220, "NIE", "Z0091418K", "700411", "25/08/2026", "NEGATIVO", "1", "Atreus", "Franco Navarro Mata", "4378636", "", "PARKO", "khato.27.01.03@gmail.com", "@el_katho", "", "No renovar", 20, 1, 2, "", "", "20263108", true),
            ParticipantEntity(221, "NIE", "Y7292978Z", "700460", "31/08/2026", "NEGATIVO", "1", "Amir", "Amir Mohammad forouzani", "699891814", "", "Harzuka", "sixymamad2@gmail.com", "@amf_z", "", "Renovar gratis", 0, 1, 5, "", "", "20263108", false),
            ParticipantEntity(222, "DNI", "05458660R", "700451", "28/08/2026", "NEGATIVO", "1", "Willardo", "Willardo Johnson Guillermo García Céspedes", "663962587", "", "MIRCO", "willardojohnsonjohn@gmail.com", "@willardojohnson", "", "Renovar gratis", 0, 1, 5, "", "", "20263108", false)
        )
    }

    fun getInitialTestRecords(): List<TestRecordEntity> {
        return listOf(
            TestRecordEntity(participantId = 1, sampleNumber = "609746", testDate = "04/06/2026", result = "NEGATIVO", periodLabel = "may/jun"),
            TestRecordEntity(participantId = 1, sampleNumber = "608486", testDate = "06/07/2026", result = "NEGATIVO", periodLabel = "jun/jul"),
            TestRecordEntity(participantId = 2, sampleNumber = "609787", testDate = "05/06/2026", result = "NEGATIVO", periodLabel = "may/jun"),
            TestRecordEntity(participantId = 2, sampleNumber = "700293", testDate = "31/07/2026", result = "NEGATIVO", periodLabel = "jul/ago"),
            TestRecordEntity(participantId = 3, sampleNumber = "606725", testDate = "22/06/2026", result = "NEGATIVO", periodLabel = "jun/jul"),
            TestRecordEntity(participantId = 3, sampleNumber = "700270", testDate = "30/07/2026", result = "NEGATIVO", periodLabel = "jul/ago"),
            TestRecordEntity(participantId = 6, sampleNumber = "430554", testDate = "11/06/2026", result = "NEGATIVO", periodLabel = "may/jun"),
            TestRecordEntity(participantId = 6, sampleNumber = "700188", testDate = "31/08/2026", result = "NEGATIVO", periodLabel = "jul/ago"),
            TestRecordEntity(participantId = 6, sampleNumber = "700470", testDate = "31/08/2026", result = "NEGATIVO", periodLabel = "ago/sep"),
            TestRecordEntity(participantId = 60, sampleNumber = "430550", testDate = "10/06/2026", result = "NEGATIVO", periodLabel = "may/jun"),
            TestRecordEntity(participantId = 60, sampleNumber = "700283", testDate = "31/08/2026", result = "NEGATIVO", periodLabel = "jul/ago"),
            TestRecordEntity(participantId = 60, sampleNumber = "700398", testDate = "31/08/2026", result = "NEGATIVO", periodLabel = "ago/sep")
        )
    }

    fun getInitialAttendances(shootCode: String = "20263108"): List<AttendanceEntity> {
        // Preset attendances for confirmed attendees with assigned locker sample
        return listOf(
            AttendanceEntity(shootCode = shootCode, participantId = 27, lockerNumber = 1, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 3, bonusPrizes = 0), // Luygy
            AttendanceEntity(shootCode = shootCode, participantId = 185, lockerNumber = 2, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Don jei
            AttendanceEntity(shootCode = shootCode, participantId = 63, lockerNumber = 4, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Empotrador
            AttendanceEntity(shootCode = shootCode, participantId = 174, lockerNumber = 5, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 1, bonusPrizes = 0), // Logan
            AttendanceEntity(shootCode = shootCode, participantId = 61, lockerNumber = 6, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 3, bonusPrizes = 0), // Dorian Black
            AttendanceEntity(shootCode = shootCode, participantId = 101, lockerNumber = 7, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 2, bonusPrizes = 0), // Acartonado
            AttendanceEntity(shootCode = shootCode, participantId = 96, lockerNumber = 8, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // Romanones
            AttendanceEntity(shootCode = shootCode, participantId = 132, lockerNumber = 9, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Sanson
            AttendanceEntity(shootCode = shootCode, participantId = 11, lockerNumber = 10, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Eduardo
            AttendanceEntity(shootCode = shootCode, participantId = 178, lockerNumber = 11, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 2, bonusPrizes = 0), // Manu
            AttendanceEntity(shootCode = shootCode, participantId = 12, lockerNumber = 12, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 1, bonusPrizes = 0), // Estu
            AttendanceEntity(shootCode = shootCode, participantId = 9, lockerNumber = 13, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // Rex
            AttendanceEntity(shootCode = shootCode, participantId = 102, lockerNumber = 14, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Christopher
            AttendanceEntity(shootCode = shootCode, participantId = 191, lockerNumber = 15, isPresent = true, contractSigned = true, initialBalance = 60, cashDeliveredAtEntry = 60, shotsCount = 3, bonusPrizes = 0), // Tony
            AttendanceEntity(shootCode = shootCode, participantId = 66, lockerNumber = 16, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Vyper
            AttendanceEntity(shootCode = shootCode, participantId = 212, lockerNumber = 17, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // Richi69
            AttendanceEntity(shootCode = shootCode, participantId = 111, lockerNumber = 18, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // El Arquitecto
            AttendanceEntity(shootCode = shootCode, participantId = 160, lockerNumber = 19, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Jemie
            AttendanceEntity(shootCode = shootCode, participantId = 219, lockerNumber = 20, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // Forgacs
            AttendanceEntity(shootCode = shootCode, participantId = 60, lockerNumber = 21, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 1, bonusPrizes = 0), // Tazmania
            AttendanceEntity(shootCode = shootCode, participantId = 208, lockerNumber = 22, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 2, bonusPrizes = 0), // Youther
            AttendanceEntity(shootCode = shootCode, participantId = 3, lockerNumber = 23, isPresent = true, contractSigned = true, initialBalance = 70, cashDeliveredAtEntry = 50, shotsCount = 2, bonusPrizes = 10, prizeReason = "Performance destacada"), // Sinner Leo with 10€ prize!
            AttendanceEntity(shootCode = shootCode, participantId = 36, lockerNumber = 24, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 2, bonusPrizes = 0), // Pedro
            AttendanceEntity(shootCode = shootCode, participantId = 216, lockerNumber = 25, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // Maikol
            AttendanceEntity(shootCode = shootCode, participantId = 8, lockerNumber = 26, isPresent = true, contractSigned = true, initialBalance = 40, cashDeliveredAtEntry = 40, shotsCount = 1, bonusPrizes = 0), // Parko
            AttendanceEntity(shootCode = shootCode, participantId = 128, lockerNumber = 27, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 1, bonusPrizes = 0), // Yohander
            AttendanceEntity(shootCode = shootCode, participantId = 220, lockerNumber = 28, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // Khato / Atreus
            AttendanceEntity(shootCode = shootCode, participantId = 34, lockerNumber = 29, isPresent = true, contractSigned = true, initialBalance = 20, cashDeliveredAtEntry = 20, shotsCount = 1, bonusPrizes = 0), // DMRC
            AttendanceEntity(shootCode = shootCode, participantId = 22, lockerNumber = 30, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 1, bonusPrizes = 0), // Maic
            AttendanceEntity(shootCode = shootCode, participantId = 206, lockerNumber = 31, isPresent = true, contractSigned = true, initialBalance = 0, cashDeliveredAtEntry = 0, shotsCount = 1, bonusPrizes = 0)  // Kardark
        )
    }
}
