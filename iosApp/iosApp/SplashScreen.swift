import SwiftUI

struct SplashScreen: View {
    @State private var isActive = false
    
    var body: some View {
        if isActive {
            ContentView()
        } else {
            VStack(spacing: 20) {
                Image("Logo")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 120, height: 120)
                
                Text("Mandarin")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.primary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(.systemBackground))
            .onAppear {
                // Переход к основному экрану через 1.5 секунды
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                    isActive = true
                }
            }
        }
    }
}

#Preview {
    SplashScreen()
}
