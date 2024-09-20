# Podcast Summary & Q&A App

[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)]()
[![Maintainer](https://img.shields.io/static/v1?label=Yevhen%20Ruban&message=Maintainer&color=red)](mailto:yevhen.ruban@extrawest.com)
[![Ask Me Anything !](https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg)]()
![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)
![GitHub release](https://img.shields.io/badge/release-v1.0.0-blue)

This project takes podcast episodes from the Podcast Index, converts the audio into text, summarizes the content, generates an image based on the summary, translates the summary into French, and allows users to ask questions about the episode. Additionally, ElevenLabs is used for audio generation.

## Features

- **Audio to Text**: Convert podcast episodes into text using Hugging Face models.
- **Summarization**: Create concise summaries of podcast episodes.
- **Translation**: Translate summarized content into French.
- **Image Generation**: Generate images based on the summarization.
- **Q&A**: Ask questions about the episode and get accurate answers.
- **Audio Creation**: Generate audio content with ElevenLabs.

## Tech Stack

- **Java 21**
- **SpringBoot 3.3.3**: Backend framework for building fast and scalable applications.
- **Hugging Face**: Provides models for transcription, summarization, and translation.
- **ElevenLabs**: Generates audio content based on summaries.
- **LangChain4J**: Orchestrates the entire process by creating a chain that integrates all functionalities.

## How It Works

1. **Fetch Podcast**: Spring Rest Client is used to retrieve podcast audio from the Podcast Index.
2. **Audio Transcription**: Hugging Face models convert the audio into text.
3. **Summarization**: The transcribed text is summarized using Hugging Face models.
4. **Translation**: The summary is translated into French using Hugging Face translation models.
5. **Image Generation**: An image is generated from the summarization using AI tools.
6. **Audio Creation**: ElevenLabs generates audio from the summarized content.
7. **Q&A**: Users can ask questions about the episode, and LangChain coordinates the response process.

## Running On Local Machine (Linux):

1. Set up MySQL on your local machine.
2. Create a database named 'podcast_analyzer'
3. Set up the following environment variables.
   - export QDRANT_GRPC_HOST=your_host;
   - export QDRANT_API_KEY==your_api_key;
   - export ELEVENLABS_API_KEY=your_api_key;
   - export PODCAST_INDEX_API_SECRET=your_api_secret;
   - export PODCAST_INDEX_API_KEY=your_api_key;
   - export HF_API_KEY=your_api_key;
4. Run the command: mvn exec:java -Dspring.profiles.active=local
5. Open the following link in your browser: http://localhost:8208/api/swagger-ui/index.html#/

## Contributing

Feel free to open issues or submit pull requests to improve the project. Contributions are welcome!

## License

This project is licensed under the MIT License.
