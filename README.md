# Event Scheduling System

![build](https://github.com/kguzek/event-scheduling-system/actions/workflows/main_event-scheduling-system.yml/badge.svg)

An efficient and flexible system for scheduling and managing events. This tool allows users to schedule events, manage attendees, and ensure no conflicts arise with pre-existing events.
Built with Java Swing & Spring Boot, this system is ideal for small and medium-sized organizations, or anyone needing a solution to manage various events and meetings.

![Tech Stack](https://github-readme-tech-stack.vercel.app/api/cards?title=Tech+Stack&align=center&lineCount=1&theme=darcula&bg=%231F1F1F&badge=%23303030&border=%23303030&titleColor=%23BB5F10&line1=spring%2CSpring+Boot%2Cauto%3Bdata%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDk2IDk2IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPg0KICAgIDxkZWZzPg0KICAgICAgICA8bGluZWFyR3JhZGllbnQgaWQ9ImUzOTljMTlmLWI2OGYtNDI5ZC1iMTc2LTE4YzIxMTdmZjczYyIgeDE9Ii0xMDMyLjE3MiIgeDI9Ii0xMDU5LjIxMyIgeTE9IjE0NS4zMTIiIHkyPSI2NS40MjYiIGdyYWRpZW50VHJhbnNmb3JtPSJtYXRyaXgoMSAwIDAgLTEgMTA3NSAxNTgpIiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSI%2BDQogICAgICAgICAgICA8c3RvcCBvZmZzZXQ9IjAiIHN0b3AtY29sb3I9IiMxMTRhOGIiLz4NCiAgICAgICAgICAgIDxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iIzA2NjliYyIvPg0KICAgICAgICA8L2xpbmVhckdyYWRpZW50Pg0KICAgICAgICA8bGluZWFyR3JhZGllbnQgaWQ9ImFjMmE2ZmMyLWNhNDgtNDMyNy05YTNjLWQ0ZGNjMzI1NmUxNSIgeDE9Ii0xMDIzLjcyNSIgeDI9Ii0xMDI5Ljk4IiB5MT0iMTA4LjA4MyIgeTI9IjEwNS45NjgiIGdyYWRpZW50VHJhbnNmb3JtPSJtYXRyaXgoMSAwIDAgLTEgMTA3NSAxNTgpIiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSI%2BDQogICAgICAgICAgICA8c3RvcCBvZmZzZXQ9IjAiIHN0b3Atb3BhY2l0eT0iLjMiLz4NCiAgICAgICAgICAgIDxzdG9wIG9mZnNldD0iLjA3MSIgc3RvcC1vcGFjaXR5PSIuMiIvPg0KICAgICAgICAgICAgPHN0b3Agb2Zmc2V0PSIuMzIxIiBzdG9wLW9wYWNpdHk9Ii4xIi8%2BDQogICAgICAgICAgICA8c3RvcCBvZmZzZXQ9Ii42MjMiIHN0b3Atb3BhY2l0eT0iLjA1Ii8%2BDQogICAgICAgICAgICA8c3RvcCBvZmZzZXQ9IjEiIHN0b3Atb3BhY2l0eT0iMCIvPg0KICAgICAgICA8L2xpbmVhckdyYWRpZW50Pg0KICAgICAgICA8bGluZWFyR3JhZGllbnQgaWQ9ImE3ZmVlOTcwLWE3ODQtNGJiMS1hZjhkLTYzZDE4ZTVmN2RiOSIgeDE9Ii0xMDI3LjE2NSIgeDI9Ii05OTcuNDgyIiB5MT0iMTQ3LjY0MiIgeTI9IjY4LjU2MSIgZ3JhZGllbnRUcmFuc2Zvcm09Im1hdHJpeCgxIDAgMCAtMSAxMDc1IDE1OCkiIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIj4NCiAgICAgICAgICAgIDxzdG9wIG9mZnNldD0iMCIgc3RvcC1jb2xvcj0iIzNjY2JmNCIvPg0KICAgICAgICAgICAgPHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjMjg5MmRmIi8%2BDQogICAgICAgIDwvbGluZWFyR3JhZGllbnQ%2BDQogICAgPC9kZWZzPg0KICAgIDxwYXRoIGZpbGw9InVybCgjZTM5OWMxOWYtYjY4Zi00MjlkLWIxNzYtMThjMjExN2ZmNzNjKSIgZD0iTTMzLjMzOCA2LjU0NGgyNi4wMzhsLTI3LjAzIDgwLjA4N2E0LjE1MiA0LjE1MiAwIDAgMS0zLjkzMyAyLjgyNEg4LjE0OWE0LjE0NSA0LjE0NSAwIDAgMS0zLjkyOC01LjQ3TDI5LjQwNCA5LjM2OGE0LjE1MiA0LjE1MiAwIDAgMSAzLjkzNC0yLjgyNXoiLz4NCiAgICA8cGF0aCBmaWxsPSIjMDA3OGQ0IiBkPSJNNzEuMTc1IDYwLjI2MWgtNDEuMjlhMS45MTEgMS45MTEgMCAwIDAtMS4zMDUgMy4zMDlsMjYuNTMyIDI0Ljc2NGE0LjE3MSA0LjE3MSAwIDAgMCAyLjg0NiAxLjEyMWgyMy4zOHoiLz4NCiAgICA8cGF0aCBmaWxsPSJ1cmwoI2FjMmE2ZmMyLWNhNDgtNDMyNy05YTNjLWQ0ZGNjMzI1NmUxNSkiIGQ9Ik0zMy4zMzggNi41NDRhNC4xMTggNC4xMTggMCAwIDAtMy45NDMgMi44NzlMNC4yNTIgODMuOTE3YTQuMTQgNC4xNCAwIDAgMCAzLjkwOCA1LjUzOGgyMC43ODdhNC40NDMgNC40NDMgMCAwIDAgMy40MS0yLjlsNS4wMTQtMTQuNzc3IDE3LjkxIDE2LjcwNWE0LjIzNyA0LjIzNyAwIDAgMCAyLjY2Ni45NzJIODEuMjRMNzEuMDI0IDYwLjI2MWwtMjkuNzgxLjAwN0w1OS40NyA2LjU0NHoiLz4NCiAgICA8cGF0aCBmaWxsPSJ1cmwoI2E3ZmVlOTcwLWE3ODQtNGJiMS1hZjhkLTYzZDE4ZTVmN2RiOSkiIGQ9Ik02Ni41OTUgOS4zNjRhNC4xNDUgNC4xNDUgMCAwIDAtMy45MjgtMi44MkgzMy42NDhhNC4xNDYgNC4xNDYgMCAwIDEgMy45MjggMi44MmwyNS4xODQgNzQuNjJhNC4xNDYgNC4xNDYgMCAwIDEtMy45MjggNS40NzJoMjkuMDJhNC4xNDYgNC4xNDYgMCAwIDAgMy45MjctNS40NzJ6Ii8%2BDQo8L3N2Zz4%3D%2CAzure%2C%3Bpostgresql%2CPostgreSQL%2Cauto%3B)

## Copyright

Copyright © 2024-2025 by Konrad Guzek

This file is part of Event Scheduling System.

Event Scheduling System is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Event Scheduling System is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
