# Movie

Описание проекта Movie:

Используемые технологии:
Spring (Boot, MVC, Test), JMS, Jackson (JSON), Log4j

Используемые классы:
Movie -- описание сущности одного фильма
Movies -- описание сущности списка фильмов
MovieController -- класс-контроллер, определяющий endpoints и обрабатывающий запросы к ним
MessageReciever -- обработка очередей
Rate -- сохранение результатов обработки очередей для выдачи пользователю при повторном запросе

Классы тестирования:
MovieTest -- основная функциональность, позитивное и негативное тестирование
MovieTestError -- тестирование обработки ошибок, полученных в процессе выполнения запросов к ресурсу themoviedb.org

Взаимодействие компонентов:

MovieController имеет 4 метода. 
3 для обработки пользовательских запросов
--  getList (url: /movie) для получения списка фильмов. Результат пользователю возвращается сразу. Принмает на вход необязательный параметр page для отображения определенной страницы списка. По дефолту отображает первую страницу.
-- getOne (url: /movie/{id}) для получения информации по определенному фильму. Результат пользователю возвращается сразу. Принимает на вход параметр id фильма для отображения.
-- getRating (url: /rating/{id}) для получения средней оценки всех фильмов в определенном жанре. Результат пользователю возващается в виде информации о прогрессе выполнения. При первом обращении -- инфмормация о принятом запросе, при повторных -- процент обработанной информации,
либо результат подсчёта, то есть средняя оценка.

1 метод для обработки исключительных ситуаций:
-- error, когда указанная информация не найдена

При получении запроса на url /rating/{id} приложение начинает обработку запроса с использованием асинхронной обработки сообщений (класс MessageReceiver, метод getRating). Текущие (или уже обработанные) результаты помещаются в поля для их обработки (класс Rate), откуда пользователь при повторном обращение к сервису получает информацию о текущем статусе обработке запроса.

Тесты включают в себя позитивные и негативные проверки. В классе MovieTestError проверяется получение и корректная обработка ошибок, полученных от ресурса themoviedb.org

Примеры запросов:

получение информации о среднем рейтинге по жанру
http://localhost:8080/rating/28
примеры корректных ответов:
{
  "status": "request accepted",
  "message": "Proccess has been started",
  "error": null
}
{
  "status": "request completed",
  "message": "Average rating: 6.0",
  "error": null
}
пример обработки ошибок от ресурса:
{
  "status": "request in progress",
  "message": "Request is in progress: 40% done",
  "error": "429 null"
}

получение информации по конкретному фильму
http://localhost:8080/movie/550
{
  "id": 550,
  "title": "Fight Club",
  "overview": "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
  "vote_average": 8
}

получение информации по списку фильмов
http://localhost:8080/movie
{
  "page": 1,
  "results": [
    {
      "id": 209112,
      "title": "Batman v Superman: Dawn of Justice",
      "overview": "Fearing the actions of a god-like Super Hero left unchecked, Gotham City’s own formidable, forceful vigilante takes on Metropolis’s most revered, modern-day savior, while the world wrestles with what sort of hero it really needs. And with Batman and Superman at war with one another, a new threat quickly arises, putting mankind in greater danger than it’s ever known before.",
      "vote_average": 5.61
    },
    {
      "id": 258489,
      "title": "The Legend of Tarzan",
      "overview": "Tarzan, having acclimated to life in London, is called back to his former home in the jungle to investigate the activities at a mining encampment.",
      "vote_average": 4.48
    },
    {
      "id": 127380,
      "title": "Finding Dory",
      "overview": "\"Finding Dory\" reunites Dory with friends Nemo and Marlin on a search for answers about her past. What can she remember? Who are her parents? And where did she learn to speak Whale?",
      "vote_average": 6.7
    },
    {
      "id": 293660,
      "title": "Deadpool",
      "overview": "Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin story of former Special Forces operative turned mercenary Wade Wilson, who after being subjected to a rogue experiment that leaves him with accelerated healing powers, adopts the alter ego Deadpool. Armed with his new abilities and a dark, twisted sense of humor, Deadpool hunts down the man who nearly destroyed his life.",
      "vote_average": 7.14
    },
    {
      "id": 47933,
      "title": "Independence Day: Resurgence",
      "overview": "We always knew they were coming back. Using recovered alien technology, the nations of Earth have collaborated on an immense defense program to protect the planet. But nothing can prepare us for the aliens’ advanced and unprecedented force. Only the ingenuity of a few brave men and women can bring our world back from the brink of extinction.",
      "vote_average": 4.69
    },
    {
      "id": 76341,
      "title": "Mad Max: Fury Road",
      "overview": "An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.",
      "vote_average": 7.32
    },
    {
      "id": 368596,
      "title": "Back in the Day",
      "overview": "A young boxer is taken under the wing of a mob boss after his mother dies and his father is run out of town for being an abusive alcoholic.",
      "vote_average": 2.54
    },
    {
      "id": 157336,
      "title": "Interstellar",
      "overview": "Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
      "vote_average": 8.16
    },
    {
      "id": 328111,
      "title": "The Secret Life of Pets",
      "overview": "The action comedy is set in a Manhattan apartment building. After the two-legged residents head for work and school, their pets gather to start their day, which consists of hanging out, trading humiliating stories about their owners, and helping each other work up adorable looks that will lead to more snacks. The head hound is a quick-witted terrier rescue (Louis C.K.), whose position at the epicenter of his master’s universe is suddenly threatened when she comes home with Duke (Stonestreet), a sloppy mongrel with no polish. The two soon find themselves on the mean streets of New York, where they meet the adorable white bunny Snowball (Hart). It turns out that Snowball is the leader of an army of pets that were abandoned and are determined to get back at humanity and every owner-loving pet. The dogs must thwart this plot and make it back in time for dinner.",
      "vote_average": 5.81
    },
    {
      "id": 262504,
      "title": "Allegiant",
      "overview": "Beatrice Prior and Tobias Eaton venture into the world outside of the fence and are taken into protective custody by a mysterious agency known as the Bureau of Genetic Welfare.",
      "vote_average": 6.06
    },
    {
      "id": 131631,
      "title": "The Hunger Games: Mockingjay - Part 1",
      "overview": "Katniss Everdeen reluctantly becomes the symbol of a mass rebellion against the autocratic Capitol.",
      "vote_average": 6.74
    },
    {
      "id": 271110,
      "title": "Captain America: Civil War",
      "overview": "Following the events of Age of Ultron, the collective governments of the world pass an act designed to regulate all superhuman activity. This polarizes opinion amongst the Avengers, causing two factions to side with Iron Man or Captain America, which causes an epic battle between former allies.",
      "vote_average": 6.91
    },
    {
      "id": 135397,
      "title": "Jurassic World",
      "overview": "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.",
      "vote_average": 6.61
    },
    {
      "id": 118340,
      "title": "Guardians of the Galaxy",
      "overview": "Light years from Earth, 26 years after being abducted, Peter Quill finds himself the prime target of a manhunt after discovering an orb wanted by Ronan the Accuser.",
      "vote_average": 7.99
    },
    {
      "id": 241251,
      "title": "The Boy Next Door",
      "overview": "A recently cheated on married woman falls for a younger man who has moved in next door, but their torrid affair soon takes a dangerous turn.",
      "vote_average": 4.62
    },
    {
      "id": 87101,
      "title": "Terminator Genisys",
      "overview": "The year is 2029. John Connor, leader of the resistance continues the war against the machines. At the Los Angeles offensive, John's fears of the unknown future begin to emerge when TECOM spies reveal a new plot by SkyNet that will attack him from both fronts; past and future, and will ultimately change warfare forever.",
      "vote_average": 5.96
    },
    {
      "id": 177572,
      "title": "Big Hero 6",
      "overview": "The special bond that develops between plus-sized inflatable robot Baymax, and prodigy Hiro Hamada, who team up with a group of friends to form a band of high-tech heroes.",
      "vote_average": 7.83
    },
    {
      "id": 269149,
      "title": "Zootopia",
      "overview": "Determined to prove herself, Officer Judy Hopps, the first bunny on Zootopia's police force, jumps at the chance to crack her first case - even if it means partnering with scam-artist fox Nick Wilde to solve the mystery.",
      "vote_average": 7.4
    },
    {
      "id": 267860,
      "title": "London Has Fallen",
      "overview": "In London for the Prime Minister's funeral, Mike Banning discovers a plot to assassinate all the attending world leaders.",
      "vote_average": 5.35
    },
    {
      "id": 140607,
      "title": "Star Wars: The Force Awakens",
      "overview": "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.",
      "vote_average": 7.55
    }
  ],
  "total_results": 19480,
  "total_pages": 974
}
