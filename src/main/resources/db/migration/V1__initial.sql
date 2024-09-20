create table podcast_episodes
(
    id                bigint        unsigned not null auto_increment primary key,
    podcast_guid      varchar(50)   not null,
    enclosure_url     varchar(255)  not null,
    enclosure_type    varchar(50)   not null,
    enclosure_length  bigint        not null,
    duration          int           not null
)
    engine = InnoDB
    collate = utf8mb4_unicode_520_ci;

create index podcast_guid_idx
    on podcast_episodes (podcast_guid);
