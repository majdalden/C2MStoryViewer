package com.majd_alden.storyviewerlibrary.utils

import com.majd_alden.storyviewerlibrary.data.Story
import com.majd_alden.storyviewerlibrary.data.StoryType
import com.majd_alden.storyviewerlibrary.data.StoryUser

object StoryGenerator {

    fun generateStories(): MutableList<StoryUser> {
        /*val storyUrls = mutableListOf<String>()
        storyUrls.add("https://miran-media.s3.amazonaws.com/resources/2020/03/23/CA_-_25.mp4")
//        storyUrls.add("https://www.youtube.com/watch?v=V1t0WOUl8Vk")
        storyUrls.add("https://player.vimeo.com/external/403295268.sd.mp4?s=3446f787cefa52e7824d6ce6501db5261074d479&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://i.picsum.photos/id/28/3840/2160.jpg?hmac=mi42yUT385MrZeW9Fq6OLmre--8-pVlylUMXJFDHiXA")
        storyUrls.add("https://player.vimeo.com/external/409206405.sd.mp4?s=0bc456b6ff355d9907f285368747bf54323e5532&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://player.vimeo.com/external/403295710.sd.mp4?s=788b046826f92983ada6e5caf067113fdb49e209&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://player.vimeo.com/external/394678700.sd.mp4?s=353646e34d7bde02ad638c7308a198786e0dff8f&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://player.vimeo.com/external/405333429.sd.mp4?s=dcc3bdec31c93d87c938fc6c3ef76b7b1b188580&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://player.vimeo.com/external/363465031.sd.mp4?s=15b706ccd3c0e1d9dc9290487ccadc7b20fff7f1&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://player.vimeo.com/external/422787651.sd.mp4?s=ec96f3190373937071ba56955b2f8481eaa10cce&profile_id=165&oauth2_token_id=57447761")
        storyUrls.add("https://images.pexels.com/photos/1433052/pexels-photo-1433052.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1366630/pexels-photo-1366630.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1067333/pexels-photo-1067333.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1122868/pexels-photo-1122868.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1837603/pexels-photo-1837603.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1612461/pexels-photo-1612461.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1591382/pexels-photo-1591382.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/53757/pexels-photo-53757.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500")
        storyUrls.add("https://images.pexels.com/photos/134020/pexels-photo-134020.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1367067/pexels-photo-1367067.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1420226/pexels-photo-1420226.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500")
        storyUrls.add("https://images.pexels.com/photos/2217365/pexels-photo-2217365.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/2260800/pexels-photo-2260800.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/1719344/pexels-photo-1719344.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/364096/pexels-photo-364096.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/3849168/pexels-photo-3849168.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/2953901/pexels-photo-2953901.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/3538558/pexels-photo-3538558.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        storyUrls.add("https://images.pexels.com/photos/2458400/pexels-photo-2458400.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")


        val userProfileUrls = mutableListOf<String>()
        userProfileUrls.add("https://randomuser.me/api/portraits/women/1.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/1.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/1.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/1.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/1.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/2.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/2.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/3.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/3.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/4.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/4.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/5.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/5.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/6.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/6.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/7.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/7.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/8.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/8.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/9.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/9.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/10.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/10.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/women/11.jpg")
        userProfileUrls.add("https://randomuser.me/api/portraits/men/11.jpg")

        val storyUserList = mutableListOf<StoryUser>()
        for (i in 1..10) {
            val stories = mutableListOf<Story>()
            val storySize = Random.nextInt(1, 5)
            for (j in 0 until storySize) {
                val storyUrl = storyUrls[Random.nextInt(storyUrls.size)].trim()
                stories.add(
                    Story(
                        storyType = if (storyUrl.contains(
                                ".mp4",
                                ignoreCase = true
                            )
                        ) StoryType.VIDEO
                        else StoryType.IMAGE,
                        storyUrl = storyUrl,
                        storyDate = System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000)
                    )
                )
            }
            storyUserList.add(
                StoryUser(
                    "username$i",
                    userProfileUrls[Random.nextInt(userProfileUrls.size)],
                    stories,
                    Random.nextBoolean()
                )
            )
        }

        val stories = mutableListOf<Story>()
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "emfvpokeropvfcre\ntgopetrkfopr\nrkfcreopfr",
                storyTextFont = StoryTextFont.CAIRO_BOLD,
                storyTextBackgroundColor = "#FFFFF000",
                storyTextColor = "#FF00479E",
                storyDate = System.currentTimeMillis() - (1 * (24 - 0) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "111HdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhfggvkvkvkvjvjvivivi222",
                storyTextFont = StoryTextFont.POPPINS_SEMI_BOLD,
                storyTextBackgroundColor = "#FF787777",
                storyTextColor = "#FFF5DE82",
                storyDate = System.currentTimeMillis() - (1 * (24 - 2) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "222HdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhhxbxhfggvkvkvkvjvjvivivi592",
                storyTextFont = StoryTextFont.SOURCE_SAN_PROBOLD,
                storyTextBackgroundColor = "#FF787777",
                storyTextColor = "#FFF5DE82",
                storyDate = System.currentTimeMillis() - (1 * (24 - 2) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "111HdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhHdhdhddjdjdhxhxhxbxhfgghhuhjjhgggggghxjchjfjjfhfhchxjdjxjjdjxhdjdhdhhxhxhhxhxhchfjdjdjdjjdjdjfjcjcjfjjfjfjfjjfjfhhhfhchhchhchhchdhhxhfhhxhdhhdhhchchdhfhdhdhdhhdhdhduduududhxhchchchhchcuhxhchchbvjkvkvkvkvkvjvjvivivi222",
                storyTextFont = StoryTextFont.ROBOTO_MEDIUM,
                storyTextBackgroundColor = "#FF787777",
                storyTextColor = "#FFF5DE82",
                storyDate = System.currentTimeMillis() - (1 * (24 - 2) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "111Lorem ipsum dolor sit amet, vestibulum accumsan22 aliquam est nulla et, commodo purus pellentesque est. Lacus phasellus praesent tincidunt nec euismod, tincidunt nulla consectetuer nullam morbi est scelerisque, diam tristique volutpat iaculis ac sapien, quam hac mollis nec dui similique ut. Vivamus enim nec tortor turpis, sit vivamus, imperdiet neque, pellentesque massa lectus nec amet nulla a, mus suscipit wisi gravida. Vivamus in dictumst, interdum egestas curabitur quisque id nibh, porttitor imperdiet orci congue volutpat arcu, vehicula vivamus urna. Sollicitudin et iaculis id nec fusce lacinia, augue nec aliquam.\n" +
                        "Metus a a ligula integer urna diam, magnis fermentum aliquam mi, torquent mauris justo molestie nonummy ut cras, leo quis. Lacus laoreet. Sem vulputate massa hendrerit. Porta massa fringilla pede aliquet nulla, consequat itaque non vivamus a. Venenatis justo consequat, consequat massa dui, amet aenean mus, tincidunt sodales turpis tempus felis tortor, hymenaeos eu. Vehicuded33la feugiat in sit elementum libero, tortor ante, vestibulum et aptent et mauris enim tincidunt, faucibus orci ante praesent amet. Phasellus fusce vestibulum suspendisse metus quia, morbi proin eu, in donec urna sagittis sint, wisi porttitor auctor dolor volutpat in pede. Duis ornare nec at, scelerisque nunc minim libero, sociis magna, in suscipit eu. Libero mus est leo, nam lorem vel, sed nibh commodi amet turpis, cursus libero turpis eu, cras eu labore elementum sem neque sem. Mi orci duis eu, vulputate vitae mi sit, vestibulum id, ullamcorper ipsum volutpat.\n" +
                        "Eu sed, rhoncus et pretium viverra, mi etiam non a volutpat, eros nunc, volutpat at. Velit nulla, vestibulum euismod consectetuer amet nec luctus pede, sed adipiscing turpis, ultricies ante lacinia, ac fringilla. Vitae tristique eros, massa elit aliquam, luctus nec donec non eros, quis condimentum nec eros, sed a. Semper luct9999us wisi mollis donec a tincidunt, quis amet orci amet vulputate urna, 3434sed praesent montes id vestibulum lobortis, quam duis dolor duis tellus, phasellus mi tortor velit. Aenean ut commodo sodales mi amet nisl. Aliquam enim. Molestie a, nec ipsum nonummy, volutpat vitae integer dignissim auctor, dolor nibh id ullamcorper natus. Sodales habitant et, sapien elit et, orci arcu, leo sed feugiat velit massa, posuere turpis nullam bibendum mi mus amet.\n" +
                        "Lacinia lorem orci amet pulvinar, a duis vel nec. Magna euismod purus viverra, potenti sed sed ultricies maecenas, sollicitudin mauris 555placerat per posuere quis, nam ante nulla lorem aliquam ege66t. Pharetra sodales, tempor diam augue l777obortis sagittis condimentum, rutrum cras p888ede facilisis, sit diam urna maecenas per nullam vivamus. Erat pellentesque tellus diam imp100erdiet turpis, nonummy integer turpis vivamus mauris. Consectetur urna, maecenas luctus, a quis aliquet risus, dignissim risus. Pede vel nibh integer, sed convallis donec turpis maecenas dolor, sed sed platea feugiat ut praesent. Viverra turpis nulla tortor at suspendisse, sapien nibh congue lacinia hendrerit lobortis, nunc erat ac. Nibh tortor risus non odio, ullamcorper voluptate, quisque iaculis blandit scelerisque rutrum pede viverra, sed id lobortis augue pretium. Sit nulla, nisl blandit nulla. Ultricies odio, vulputate aliquam massa diam proin, quis luctus faucibus pellentesque etiam amet, donec eu. Eget amet justo, ligula aenean, semper nullam felis at dapibus pede.",
                storyTextFont = StoryTextFont.POPPINS_LIGHT,
                storyTextBackgroundColor = "#FFA23A3A",
                storyTextColor = "#FFADE978",
                maxStoryTextLength = 250,
                maxStoryTextLines = 8,
                storyDate = System.currentTimeMillis() - (1 * (24 - 2) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n",
                storyTextFont = StoryTextFont.SF_PRO_DISPLAY_MEDIUM,
                storyTextBackgroundColor = "#000000",
                storyTextColor = "#FFFFFF",
                maxStoryTextLines = 12,
                storyDate = System.currentTimeMillis() - (1 * (24 - 1) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n",
                storyTextFont = StoryTextFont.SOURCE_SAN_PRO_SEMIBOLD,
                storyTextBackgroundColor = "#FFFFFF",
                storyTextColor = "#000000",
                maxStoryTextLines = 12,
                storyDate = System.currentTimeMillis() - (1 * (24 - 2) * 60 * 60 * 1000)
            )
        )
        stories.add(
            Story(
                storyType = StoryType.TEXT,
                storyText = "1\n2\n3\n4\n5\n6\n7\n8\n9\n10",
                storyTextFont = StoryTextFont.APP_ROBOTO_BOLD,
                storyTextBackgroundColor = "#FFE96161",
                storyTextColor = "#FFD9FF92",
                maxStoryTextLines = 10,
                storyDate = System.currentTimeMillis() - (1 * (24 - 2) * 60 * 60 * 1000)
            )
        )

        storyUserList.add(
            0,
            StoryUser(
                "username$0",
                userProfileUrls[Random.nextInt(userProfileUrls.size)],
                stories,
                Random.nextBoolean()
            )
        )*/


        val storyUserList = mutableListOf<StoryUser>()

 //        val story = Story(
 //            storyType = StoryType.VIDEO,
 //            storyUrl = "https://miran-media.s3.amazonaws.com/resources/2020/03/23/CA_-_25.mp4",
 //            storyDate = System.currentTimeMillis() - (1 * (24 - 5) * 60 * 60 * 1000)
 //        )
        /*val story = Story(
            storyType = StoryType.IMAGE,
//            storyUrl = "https://images.pexels.com/photos/1433052/pexels-photo-1433052.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
//            storyUrl = "https://images.pexels.com/photos/1591382/pexels-photo-1591382.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
//            storyUrl = "https://picsum.photos/3840/2160",
            storyUrl = "https://i.picsum.photos/id/537/3840/2160.jpg?hmac=E58kTVbK24I6pe33UunljK-5ciq2NM3ktMeBagt_VnQ",
            storyDate = System.currentTimeMillis() - (1 * (24 - 5) * 60 * 60 * 1000)
        )

       val story2 = Story(
           storyType = StoryType.IMAGE,
           //            storyUrl = "https://images.pexels.com/photos/1433052/pexels-photo-1433052.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
           //            storyUrl = "https://images.pexels.com/photos/1591382/pexels-photo-1591382.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
           //            storyUrl = "https://picsum.photos/3840/2160",
           storyUrl = "https://i.picsum.photos/id/329/3840/2160.jpg?hmac=jsjwZF7XRBrLZH9fAhFo5unttOnfQOxXvi_vrktqL2c",
           storyDate = System.currentTimeMillis() - (1 * (24 - 5) * 60 * 60 * 1000)
       )

       val story3 = Story(
           storyType = StoryType.IMAGE,
           //            storyUrl = "https://images.pexels.com/photos/1433052/pexels-photo-1433052.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
           //            storyUrl = "https://images.pexels.com/photos/1591382/pexels-photo-1591382.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
           //            storyUrl = "https://picsum.photos/3840/2160",
           storyUrl = "https://i.picsum.photos/id/865/3840/2160.jpg?hmac=x8_MZzQKgdZDahDdCE3X-Hsa2_qap5EpyO1tgInEAHs",
           storyDate = System.currentTimeMillis() - (1 * (24 - 5) * 60 * 60 * 1000)
       )

       val story4 = Story(
           storyType = StoryType.IMAGE,
           //            storyUrl = "https://images.pexels.com/photos/1433052/pexels-photo-1433052.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
           //            storyUrl = "https://images.pexels.com/photos/1591382/pexels-photo-1591382.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
           //            storyUrl = "https://picsum.photos/3840/2160",
           storyUrl = "https://i.picsum.photos/id/386/3840/2160.jpg?hmac=uZtyARY2dNxoecEn-JeAu28INr8Hq065K8taAzHXKbY",
           storyDate = System.currentTimeMillis() - (1 * (24 - 5) * 60 * 60 * 1000)
       )*/

        val story = Story(
            storyType = StoryType.IMAGE,
            storyUrl = "https://app.hony.us/story/file/8883b162-1f80-4fb2-bb88-436a47ce1712.jpg",
            storyDate = System.currentTimeMillis() - (1 * (24 - 5) * 60 * 60 * 1000)
        )

        storyUserList.add(
            0,
            StoryUser(
                "username$0",
                "https://randomuser.me/api/portraits/women/1.jpg",
                mutableListOf(story/*,story2,story3,story4*/),
                true
            )
        )

        return storyUserList
    }
}